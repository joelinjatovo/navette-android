package com.navetteclub.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.snackbar.Snackbar;
import com.navetteclub.R;
import com.navetteclub.database.callback.UpsertCallback;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentLoginBinding;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.LoginViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.UserViewModel;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LoginFragment extends Fragment implements TextWatcher {

    private static final String TAG = LoginFragment.class.getSimpleName();

    private FragmentLoginBinding mBinding;

    private LoginViewModel loginViewModel;

    private UserViewModel userViewModel;

    private AuthViewModel authViewModel;

    private ProgressDialog progressDialog;

    private CallbackManager callbackManager;

    private AccessTokenTracker accessTokenTracker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

        MyViewModelFactory factory = new MyViewModelFactory(requireActivity().getApplication());

        loginViewModel = new ViewModelProvider(requireActivity(), factory).get(LoginViewModel.class);

        userViewModel = new ViewModelProvider(requireActivity(), factory).get(UserViewModel.class);

        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);

        final NavController navController = Navigation.findNavController(view);
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        Log.d(TAG, "'AUTHENTICATED'");
                        navController.popBackStack();
                    }
                });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        //authViewModel.logout(requireContext());
                        navController.popBackStack();
                    }
                });

        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(),
                loginFormState -> {
                    if (loginFormState == null) {
                        return;
                    }

                    mBinding.loginButton.setEnabled(loginFormState.isDataValid());

                    if (loginFormState.getPhoneError() != null) {
                        mBinding.phoneEditText.setError(getString(loginFormState.getPhoneError()));
                    }

                    if (loginFormState.getPasswordError() != null) {
                        mBinding.passwordEditText.setError(getString(loginFormState.getPasswordError()));
                    }
                });

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(),
                loginResult -> {
                    if (loginResult == null) {
                        return;
                    }

                    progressDialog.hide();

                    if (loginResult.getError() != null) {
                        Log.d(TAG, "'loginResult.getError()'");
                        Snackbar.make(mBinding.getRoot(), loginResult.getError(), Snackbar.LENGTH_SHORT).show();
                    }

                    if (loginResult.getSuccess() != null) {
                        Log.d(TAG, "'loginResult.getSuccess()'");
                        userViewModel.upsert(upsertCallback, loginResult.getSuccess());
                    }

                    // Reset remote result
                    loginViewModel.setLoginResult(null);
                });

        mBinding.phoneEditText.addTextChangedListener(this);

        mBinding.passwordEditText.addTextChangedListener(this);

        mBinding.passwordEditText.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if(mBinding.loginButton.isEnabled()) {
                            progressDialog.show();
                            loginViewModel.login(mBinding.phoneCountryCodeSpinner.getSelectedItem().toString() + mBinding.phoneEditText.getText().toString(), mBinding.passwordEditText.getText().toString());
                        }
                    }
                    return false;
                });

        mBinding.backButton.setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_login_to_home);
                });

        mBinding.loginButton.setOnClickListener(
                v -> {
                    //loadingProgressBar.setVisibility(View.VISIBLE);
                    progressDialog.show();
                    loginViewModel.login(mBinding.phoneCountryCodeSpinner.getSelectedItem().toString() + mBinding.phoneEditText.getText().toString(), mBinding.passwordEditText.getText().toString());
                });

        mBinding.registerButton.setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.navigation_register);
                });

        mBinding.forgotButton.setOnClickListener(
                v -> {
                    ForgotFragment fragment = new ForgotFragment();
                    fragment.show(getParentFragmentManager(), fragment.getTag());
                });

        setupFacebookConnect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();

        //This starts the access token tracking
        accessTokenTracker.startTracking();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            useLoginInformation(accessToken);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // We stop the tracking before destroying the activity
        accessTokenTracker.stopTracking();
    }

    private void setupFacebookConnect() {
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = mBinding.facebookConnect;
        loginButton.setPermissions("email", "public_profile");
        // If using in a fragment
        loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                AccessToken accessToken = loginResult.getAccessToken();
                useLoginInformation(accessToken);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.e(TAG, exception.getMessage(), exception);
            }
        });

        // Defining the AccessTokenTracker
        accessTokenTracker = new AccessTokenTracker() {
            // This method is invoked everytime access token changes
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                useLoginInformation(currentAccessToken);
            }
        };
    }

    private void useLoginInformation(AccessToken accessToken) {
        /**
         Creating the GraphRequest to fetch user details
         1st Param - AccessToken
         2nd Param - Callback (which will be invoked once the request is successful)
         **/
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String name = object.getString("name");
                    String email = object.getString("email");
                    String image = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    Log.d(TAG + "Facebook", name + " " + email + " " + image);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        loginViewModel.loginDataChanged(
                mBinding.phoneEditText.getText().toString(),
                mBinding.passwordEditText.getText().toString()
            );
    }

    private UpsertCallback<User> upsertCallback = new UpsertCallback<User>() {
        @Override
        public void onUpsertError() {
            Toast.makeText(getContext(), getString(R.string.error_database), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onUpsertSuccess(List<User> users) {
            User user = users.get(0);
            Preferences.Auth.setCurrentUser(getContext(), user);
            authViewModel.authenticate(user);
            updateUiWithUser(user);
        }

        private void updateUiWithUser(User user) {
            String welcome = getString(R.string.welcome) + user.getName();
            Toast.makeText(getContext(), welcome, Toast.LENGTH_LONG).show();
        }
    };
}

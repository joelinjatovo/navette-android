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
import androidx.navigation.fragment.NavHostFragment;

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
import com.navetteclub.api.models.Register;
import com.navetteclub.database.callback.UpsertCallback;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentLoginBinding;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.LoginViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.RegisterViewModel;
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

    private RegisterViewModel registerViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = NavHostFragment.findNavController(this);
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity(), factory).get(UserViewModel.class);
        loginViewModel = new ViewModelProvider(requireActivity(), factory).get(LoginViewModel.class);
        setupLoginViewModel();
        setupAuthViewModel(navController);
        setupRegisterViewModel(factory);
        setupBackAction(navController);
        setupUi();
        setupFacebookConnect();
    }

    private void setupRegisterViewModel(MyViewModelFactory factory) {
        registerViewModel = new ViewModelProvider(requireActivity(), factory).get(RegisterViewModel.class);
        registerViewModel.getRegisterResult().observe(getViewLifecycleOwner(),
                registerResult -> {
                    if (registerResult == null) {
                        return;
                    }

                    progressDialog.dismiss();

                    if (registerResult.getError() != null) {
                        Log.d(TAG, "'registerResult.getError()'");
                        Snackbar.make(mBinding.getRoot(), registerResult.getError(), Snackbar.LENGTH_SHORT).show();
                    }

                    if (registerResult.getSuccess() != null) {
                        Log.d(TAG, "'registerResult.getSuccess()'");
                        userViewModel.upsert(upsertCallback, registerResult.getSuccess());
                    }

                    // Reset remote result
                    registerViewModel.setRegisterResult(null);
                });
    }

    private void setupAuthViewModel(NavController navController) {
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    // Nothing
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        Log.d(TAG, "'AUTHENTICATED'");
                        User user = authViewModel.getUser();
                        if (user != null) {
                            if (user.getPhone() == null) {
                                navController.navigate(R.id.action_login_fragment_to_phone_fragment);
                            } else if (!user.getVerified()) {
                                navController.navigate(R.id.action_login_fragment_to_verify_phone_fragment);
                            } else {
                                navController.popBackStack();
                            }
                        }
                    }
                });
    }

    private void setupLoginViewModel() {
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
                        Snackbar.make(mBinding.getRoot(), loginResult.getError(), Snackbar.LENGTH_SHORT).show();
                    }
                    if (loginResult.getSuccess() != null) {
                        userViewModel.upsert(upsertCallback, loginResult.getSuccess());
                    }
                    // Reset remote result
                    loginViewModel.setLoginResult(null);
                });
    }

    private void setupUi() {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));
        mBinding.phoneEditText.addTextChangedListener(this);
        mBinding.passwordEditText.addTextChangedListener(this);
        mBinding.passwordEditText.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if(mBinding.loginButton.isEnabled()) {
                            progressDialog.show();
                            loginViewModel.login(
                                    mBinding.phoneCountryCodeSpinner.getSelectedItem().toString() + mBinding.phoneEditText.getText().toString(),
                                    mBinding.passwordEditText.getText().toString());
                        }
                    }
                    return false;
                });
        mBinding.loginButton.setOnClickListener(
                v -> {
                    progressDialog.show();
                    loginViewModel.login(
                            mBinding.phoneCountryCodeSpinner.getSelectedItem().toString() + mBinding.phoneEditText.getText().toString(),
                            mBinding.passwordEditText.getText().toString());
                });
        mBinding.registerButton.setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_login_fragment_to_register_fragment);
                });
        mBinding.forgotButton.setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_login_fragment_to_forgot_fragment);
                });
        mBinding.facebookLogin.setOnClickListener(
                v -> {
                    mBinding.facebookConnect.performClick();
                });
    }

    private void setupBackAction(NavController navController) {
        mBinding.backButton.setOnClickListener(v -> navController.popBackStack(R.id.navigation_home, false));
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navController.popBackStack(R.id.navigation_home, false);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
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
                Log.d(TAG, "FacebookCallback.onSuccess(loginResult)");
                // App code
                AccessToken accessToken = loginResult.getAccessToken();
                useLoginInformation(accessToken);
            }

            @Override
            public void onCancel() {
                // App code
                Log.d(TAG, "FacebookCallback.onCancel()");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d(TAG, "FacebookCallback.onError(exception)");
                Log.e(TAG, exception.getMessage(), exception);
            }
        });
    }

    private void useLoginInformation(AccessToken accessToken) {
        Log.d(TAG, "useLoginInformation(accessToken)");
        progressDialog.show();
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    Log.d(TAG, "JSONObject="  + object);
                    if(object!=null) {
                        Register registrationData = new Register();
                        registrationData.facebookId = object.getString("id");
                        registrationData.name = object.getString("name");
                        registrationData.email = object.getString("email");
                        registrationData.pictureUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");

                        Log.d(TAG, "registrationData="  + registrationData);
                        registerViewModel.registerViaFacebook(registrationData);
                    }
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
            Log.d(TAG, "UpsertCallback.onUpsertSuccess(users)");
            User user = users.get(0);
            Preferences.Auth.setCurrentUser(requireContext(), user);
            authViewModel.authenticate(user);
            updateUiWithUser(user);
        }

        private void updateUiWithUser(User user) {
            String welcome = getString(R.string.welcome) + user.getName();
            Toast.makeText(getContext(), welcome, Toast.LENGTH_LONG).show();
        }
    };
}

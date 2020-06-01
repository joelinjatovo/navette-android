package com.navetteclub.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.facebook.AccessToken;
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
import com.navetteclub.databinding.FragmentRegisterBinding;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.Preferences;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.RegisterViewModel;
import com.navetteclub.vm.UserViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RegisterFragment  extends Fragment {

    private static final String TAG = RegisterFragment.class.getSimpleName();

    private FragmentRegisterBinding mBinding;

    private RegisterViewModel registerViewModel;

    private UserViewModel userViewModel;

    private AuthViewModel authViewModel;

    private ProgressDialog progressDialog;

    private CallbackManager callbackManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = NavHostFragment.findNavController(this);
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity(), factory).get(UserViewModel.class);
        registerViewModel = new ViewModelProvider(this, factory).get(RegisterViewModel.class);
        setupUi();
        setupBackAction(navController);
        setupFacebookConnect();
        setupAuthViewModel(navController);
        setupRegisterViewModel();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(callbackManager!=null){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupUi() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));
        mBinding.registerButton.setOnClickListener(
                v -> {
                    progressDialog.show();
                    registerViewModel.register(mBinding.nameEditText.getText().toString(),
                            mBinding.phoneCountryCodeSpinner.getSelectedItem().toString() + mBinding.phoneEditText.getText().toString(),
                            mBinding.passwordEditText.getText().toString());
                });
        mBinding.loginButton.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        mBinding.facebookLogin.setOnClickListener(v -> mBinding.facebookConnect.performClick());
    }

    private void setupRegisterViewModel() {
        registerViewModel.getRegisterResult().observe(getViewLifecycleOwner(),
                registerResult -> {
                    if (registerResult == null) {
                        return;
                    }

                    progressDialog.dismiss();

                    if (registerResult.getError() != null) {
                        Snackbar.make(mBinding.getRoot(), registerResult.getError(), Snackbar.LENGTH_SHORT).show();
                    }

                    if (registerResult.getSuccess() != null) {
                        userViewModel.upsert(upsertCallback, registerResult.getSuccess());
                    }

                    // Reset remote result
                    registerViewModel.setRegisterResult(null);
                });
    }

    private void setupAuthViewModel(NavController navController) {
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        User user = authViewModel.getUser();
                        if (user != null) {
                            if (user.getPhone() == null) {
                                navController.navigate(R.id.action_register_fragment_to_phone_fragment);
                            } else if (!user.getVerified()) {
                                navController.navigate(R.id.action_register_fragment_to_verify_phone_fragment);
                            } else {
                                navController.popBackStack();
                            }
                        }
                    }
                });
    }

    private void setupBackAction(NavController navController) {
        mBinding.backButton.setOnClickListener( v-> navController.popBackStack());
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        navController.popBackStack();
                    }
                });
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
                Log.e(TAG, "FacebookCallback.onError(exception)", exception);
            }
        });
    }

    private void useLoginInformation(AccessToken accessToken) {
        progressDialog.show();
        //OnCompleted is invoked once the GraphRequest is successful
        GraphRequest request = GraphRequest.newMeRequest(accessToken, (object, response) -> {
            try {
                if(object!=null) {
                    Register registrationData = new Register();
                    registrationData.facebookId = object.getString("id");
                    registrationData.name = object.getString("name");
                    registrationData.email = object.getString("email");
                    registrationData.pictureUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    registerViewModel.registerViaFacebook(registrationData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
    }

    private UpsertCallback<User> upsertCallback = new UpsertCallback<User>() {
        @Override
        public void onUpsertError() {
            Toast.makeText(getContext(), getString(R.string.error_database), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onUpsertSuccess(List<User> users) {
            if((users!=null) && (users.size() > 0)){
                User user = users.get(0);
                Preferences.Auth.setCurrentUser(requireContext(), user);
                authViewModel.authenticate(user);
            }
        }
    };
}

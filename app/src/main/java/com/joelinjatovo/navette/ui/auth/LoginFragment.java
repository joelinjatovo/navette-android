package com.joelinjatovo.navette.ui.auth;

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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.snackbar.Snackbar;
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.database.callback.UpsertCallback;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.database.entity.UserWithRoles;
import com.joelinjatovo.navette.databinding.FragmentLoginBinding;
import com.joelinjatovo.navette.vm.AuthViewModel;
import com.joelinjatovo.navette.vm.LoginViewModel;
import com.joelinjatovo.navette.vm.MyViewModelFactory;
import com.joelinjatovo.navette.vm.UserViewModel;
import com.joelinjatovo.navette.utils.Log;
import com.joelinjatovo.navette.utils.Preferences;

import java.util.List;

public class LoginFragment extends Fragment implements TextWatcher {

    private static final String TAG = LoginFragment.class.getSimpleName();

    private FragmentLoginBinding mBinding;

    private LoginViewModel loginViewModel;

    private UserViewModel userViewModel;

    private AuthViewModel authViewModel;

    private ProgressDialog progressDialog;

    private CallbackManager callbackManager;

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
                        userViewModel.insertUserWithRoles(upsertCallback, loginResult.getSuccess());
                    }
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

    private void setupFacebookConnect() {
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = mBinding.facebookConnect;
        loginButton.setPermissions("email");
        // If using in a fragment
        loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
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

    private UpsertCallback<UserWithRoles> upsertCallback = new UpsertCallback<UserWithRoles>() {
        @Override
        public void onUpsertError() {
            Toast.makeText(getContext(), getString(R.string.error_database), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onUpsertSuccess(List<UserWithRoles> users) {
            Preferences.Auth.setCurrentUser(getContext(), users.get(0).getUser());
            authViewModel.authenticate(users.get(0).getUser());
            updateUiWithUser(users.get(0).getUser());
        }

        private void updateUiWithUser(User model) {
            String welcome = getString(R.string.welcome) + model.getName();
            Toast.makeText(getContext(), welcome, Toast.LENGTH_LONG).show();
        }
    };
}

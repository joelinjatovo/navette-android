package com.joelinjatovo.navette.ui.main.auth;

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
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.database.repository.UserRepository;
import com.joelinjatovo.navette.databinding.FragmentLoginBinding;
import com.joelinjatovo.navette.ui.auth.forgot.ForgotFragment;
import com.joelinjatovo.navette.ui.auth.login.LoginActivity;
import com.joelinjatovo.navette.ui.auth.login.LoginViewModel;
import com.joelinjatovo.navette.ui.auth.login.LoginViewModelFactory;
import com.joelinjatovo.navette.ui.auth.register.RegisterActivity;
import com.joelinjatovo.navette.ui.maps.MapsActivity;
import com.joelinjatovo.navette.ui.vm.UserViewModel;
import com.joelinjatovo.navette.ui.vm.UserViewModelFactory;
import com.joelinjatovo.navette.utils.Preferences;

import java.util.List;

public class LoginFragment extends Fragment implements TextWatcher {

    private FragmentLoginBinding mBinding;

    private AuthViewModel authViewModel;

    private UserViewModel userViewModel;

    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

        authViewModel = new ViewModelProvider(this, new AuthViewModelFactory()).get(AuthViewModel.class);

        userViewModel = new ViewModelProvider(this, new UserViewModelFactory(getActivity().getApplication())).get(UserViewModel.class);

        final NavController navController = Navigation.findNavController(view);
        final View root = view;
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    switch (authenticationState) {
                        case AUTHENTICATED:
                            navController.popBackStack();
                            break;
                        case INVALID_AUTHENTICATION:
                            Snackbar.make(root,
                                    R.string.invalid_credentials,
                                    Snackbar.LENGTH_SHORT
                            ).show();
                            break;
                    }
                });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        authViewModel.refuseAuthentication();
                        navController.popBackStack(R.id.nav_host_fragment, false);
                    }
                });

        authViewModel.getLoginFormState().observe(getViewLifecycleOwner(),
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

        authViewModel.getLoginResult().observe(getViewLifecycleOwner(),
                loginResult -> {
                    if (loginResult == null) {
                        return;
                    }

                    progressDialog.hide();

                    if (loginResult.getError() != null) {
                        showLoginFailed(loginResult.getError());
                    }

                    if (loginResult.getSuccess() != null) {
                        setLoggedInUser(loginResult.getSuccess());
                    }
                });

        mBinding.phoneEditText.addTextChangedListener(this);

        mBinding.passwordEditText.addTextChangedListener(this);

        mBinding.passwordEditText.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if(mBinding.loginButton.isEnabled()) {
                            progressDialog.show();
                            authViewModel.login(mBinding.phoneCountryCodeSpinner.getSelectedItem().toString() + mBinding.phoneEditText.getText().toString(), mBinding.passwordEditText.getText().toString());
                        }
                    }
                    return false;
                });

        mBinding.loginButton.setOnClickListener(
                v -> {
                    //loadingProgressBar.setVisibility(View.VISIBLE);
                    progressDialog.show();
                    authViewModel.login(mBinding.phoneCountryCodeSpinner.getSelectedItem().toString() + mBinding.phoneEditText.getText().toString(), mBinding.passwordEditText.getText().toString());
                });

        mBinding.registerButton.setOnClickListener(
                v -> {
                    Intent intent = new Intent(getContext(), RegisterActivity.class);
                    startActivity(intent);
                    //this.finish();
                });

        mBinding.forgotButton.setOnClickListener(
                v -> {
                    ForgotFragment fragment = new ForgotFragment();
                    fragment.show(getParentFragmentManager(), fragment.getTag());
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
        authViewModel.loginDataChanged(
                mBinding.phoneEditText.getText().toString(),
                mBinding.passwordEditText.getText().toString()
            );
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    private void setLoggedInUser(User user) {
        userViewModel.insert(insertUserCallback, user);
    }

    private UserRepository.Callback insertUserCallback = new UserRepository.Callback() {
        @Override
        public void onError() {
            Toast.makeText(getContext(), getString(R.string.error_database), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSuccess(List<User> users) {
            Preferences.Auth.setCurrentUser(getContext(), users.get(0));
            updateUiWithUser(users.get(0));
        }

        private void updateUiWithUser(User model) {
            String welcome = getString(R.string.welcome) + model.getName();
            Toast.makeText(getContext(), welcome, Toast.LENGTH_LONG).show();
        }
    };
}

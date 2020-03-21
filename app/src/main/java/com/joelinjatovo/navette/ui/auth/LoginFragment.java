package com.joelinjatovo.navette.ui.auth;

import android.app.ProgressDialog;
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
        final View root = view;
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
                        authViewModel.refuseAuthentication();
                        navController.popBackStack(R.id.nav_host_fragment, false);
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
                        Snackbar.make(mBinding.getRoot(), loginResult.getError(), Snackbar.LENGTH_SHORT).show();
                    }

                    if (loginResult.getSuccess() != null) {
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

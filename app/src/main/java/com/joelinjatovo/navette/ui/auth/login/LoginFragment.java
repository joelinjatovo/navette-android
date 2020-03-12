package com.joelinjatovo.navette.ui.auth.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding mBinding;

    private LoginViewModel loginViewModel;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);

        return mBinding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
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
            }
        });

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }

                //loadingProgressBar.setVisibility(View.GONE);

                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }

                if (loginResult.getSuccess() != null) {
                    setLoggedInUser(loginResult.getSuccess());
                    updateUiWithUser(loginResult.getSuccess());
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(mBinding.phoneEditText.getText().toString(), mBinding.passwordEditText.getText().toString());
            }
        };
        mBinding.phoneEditText.addTextChangedListener(afterTextChangedListener);
        mBinding.passwordEditText.addTextChangedListener(afterTextChangedListener);
        mBinding.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(mBinding.loginButton.isEnabled()) {
                        //progressDialog.show();
                        loginViewModel.login(mBinding.phoneEditText.getText().toString(), mBinding.passwordEditText.getText().toString());
                    }
                }
                return false;
            }
        });

        mBinding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadingProgressBar.setVisibility(View.VISIBLE);
                //progressDialog.show();
                loginViewModel.login(mBinding.phoneEditText.getText().toString(), mBinding.passwordEditText.getText().toString());
            }
        });
    }

    private void setLoggedInUser(User user) {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    private void updateUiWithUser(User model) {
        String welcome = getString(R.string.welcome) + model.getName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getContext(), errorString, Toast.LENGTH_SHORT).show();
    }

}
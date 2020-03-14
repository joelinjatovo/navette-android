package com.joelinjatovo.navette.ui.auth.register;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.databinding.ActivityRegisterBinding;
import com.joelinjatovo.navette.ui.auth.login.LoginActivity;
import com.joelinjatovo.navette.utils.Log;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding mBinding;

    private RegisterViewModel registerViewModel;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

        registerViewModel = new ViewModelProvider(this, new RegisterViewModelFactory()).get(RegisterViewModel.class);

        registerViewModel.getRegisterResult().observe(this, registerResult -> {
            if (registerResult == null) {
                return;
            }

            progressDialog.hide();

            if (registerResult.getError() != null) {
                showRegisterFailed(registerResult.getError());
            }

            if (registerResult.getSuccess() != null) {
                setRegisteredInUser(registerResult.getSuccess());
                updateUiWithUser(registerResult.getSuccess());
            }
        });

        mBinding.registerButton.setOnClickListener(v -> {
            //loadingProgressBar.setVisibility(View.VISIBLE);
            progressDialog.show();
            registerViewModel.register(mBinding.nameEditText.getText().toString(), mBinding.phoneCountryCodeSpinner.getSelectedItem().toString() + mBinding.phoneEditText.getText().toString(), mBinding.passwordEditText.getText().toString());
        });

        mBinding.loginButton.setOnClickListener(v->{
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        });

        mBinding.backButton.setOnClickListener(v->{
            onBackPressed();
        });
    }

    private void setRegisteredInUser(User user) {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    private void updateUiWithUser(User model) {
        String welcome = getString(R.string.welcome) + model.getName();
        // TODO : initiate successful logged in experience
        Toast.makeText(this, welcome, Toast.LENGTH_LONG).show();
    }

    private void showRegisterFailed(@StringRes Integer errorString) {
        Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();
    }
}

package com.joelinjatovo.navette.ui.auth.login;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.databinding.ActivityLoginBinding;
import com.joelinjatovo.navette.ui.auth.forgot.ForgotFragment;
import com.joelinjatovo.navette.ui.auth.register.RegisterActivity;
import com.joelinjatovo.navette.ui.maps.MapsActivity;
import com.joelinjatovo.navette.utils.Constants;
import com.joelinjatovo.navette.utils.Log;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mBinding;

    private LoginViewModel loginViewModel;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
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

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }

            progressDialog.hide();

            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }

            if (loginResult.getSuccess() != null) {
                setLoggedInUser(loginResult.getSuccess());
                updateUiWithUser(loginResult.getSuccess());
                openMapActivity();
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

        mBinding.passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if(mBinding.loginButton.isEnabled()) {
                    progressDialog.show();
                    loginViewModel.login(mBinding.phoneCountryCodeSpinner.getSelectedItem().toString() + mBinding.phoneEditText.getText().toString(), mBinding.passwordEditText.getText().toString());
                }
            }
            return false;
        });

        mBinding.loginButton.setOnClickListener(v -> {
            //loadingProgressBar.setVisibility(View.VISIBLE);
            progressDialog.show();
            loginViewModel.login(mBinding.phoneCountryCodeSpinner.getSelectedItem().toString() + mBinding.phoneEditText.getText().toString(), mBinding.passwordEditText.getText().toString());
        });

        mBinding.registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            //this.finish();
        });

        mBinding.forgotButton.setOnClickListener(v -> {
            ForgotFragment fragment = new ForgotFragment();
            fragment.show(getSupportFragmentManager(), fragment.getTag());
        });

        mBinding.backButton.setOnClickListener(v->{
            onBackPressed();
        });
    }

    private void connectPush() {
        PusherOptions options = new PusherOptions();
        options.setCluster(Constants.PUSHER_APP_CLUSTER);

        Pusher pusher = new Pusher(Constants.PUSHER_APP_KEY, options);
        pusher.connect();

        Channel channel = pusher.subscribe("my-channel");

        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.d("PUSHER_APP", event.getData());
            }
        });
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();
    }

    private void setLoggedInUser(User user) {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    private void updateUiWithUser(User model) {
        String welcome = getString(R.string.welcome) + model.getName();
        // TODO : initiate successful logged in experience
        Toast.makeText(this, welcome, Toast.LENGTH_LONG).show();
    }

    private void openMapActivity() {
        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
        startActivity(intent);
    }
}

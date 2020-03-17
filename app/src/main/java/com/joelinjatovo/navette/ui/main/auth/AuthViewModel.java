package com.joelinjatovo.navette.ui.main.auth;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.data.repositories.LoginRepository;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.ui.main.auth.login.LoginResult;
import com.joelinjatovo.navette.ui.main.auth.login.LoginFormState;

import java.util.Date;

public class AuthViewModel extends ViewModel {

    public enum AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,          // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    private User user;

    private final MutableLiveData<AuthenticationState> authenticationState = new MutableLiveData<>();

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();

    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    private LoginRepository loginRepository;

    AuthViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MutableLiveData<AuthenticationState> getAuthenticationState() {
        return authenticationState;
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void authenticate(User user) {
        if (!isTokenExpired(user)) {
            authenticationState.setValue(AuthenticationState.AUTHENTICATED);
        } else {
            authenticationState.setValue(AuthenticationState.INVALID_AUTHENTICATION);
        }
    }

    public void refuseAuthentication() {
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
    }

    public void login(String phone, String password) {
        // launch in an asynchronous job
        loginRepository.login(phone, password, loginResult);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    private boolean isTokenExpired(User user) {
        return new Date().after(user.getTokenExpires());
    }
}

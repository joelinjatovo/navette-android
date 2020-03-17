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
import com.joelinjatovo.navette.utils.Log;

import java.io.IOException;
import java.util.Date;

public class AuthViewModel extends ViewModel {

    public enum AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,          // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    private User user;

    private final MutableLiveData<AuthenticationState> authenticationState = new MutableLiveData<>();

    AuthViewModel() {
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

    private boolean isTokenExpired(User user) {
        return new Date().after(user.getTokenExpires());
    }
}

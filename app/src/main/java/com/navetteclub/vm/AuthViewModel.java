package com.navetteclub.vm;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.facebook.login.LoginManager;
import com.navetteclub.App;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.Register;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.UserApiService;
import com.navetteclub.database.callback.FindCallback;
import com.navetteclub.database.entity.User;
import com.navetteclub.database.repositories.UserRepository;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.Preferences;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends ViewModel implements FindCallback<User> {

    private static final String TAG = AuthViewModel.class.getSimpleName();

    private UserRepository userRepository;

    private User user = null;

    public enum AuthenticationState {
        UNAUTHENTICATED,        // Initial state, the user needs to authenticate
        AUTHENTICATED,          // The user has authenticated successfully
        INVALID_AUTHENTICATION  // Authentication failed
    }

    private final MutableLiveData<AuthenticationState> authenticationState = new MutableLiveData<>();

    AuthViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public void isAuthenticated(Long userId) {
        userRepository.find(this, userId);
    }

    public void authenticate(User user) {
        Log.d(TAG, "user = " + user);
        setUser(user);
        if(user == null) {
            authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
        }else if (!isTokenExpired(user)) {
            authenticationState.setValue(AuthenticationState.AUTHENTICATED);
        } else {
            authenticationState.setValue(AuthenticationState.INVALID_AUTHENTICATION);
        }
    }

    public void logout(Context context) {
        Preferences.Auth.setCurrentUser(context, 0L); // app
        Preferences.Auth.setCurrentToken(context, null); // app
        App.stopPushService(); // pusher
        LoginManager.getInstance().logOut(); // facebook
        authenticate(null);
    }

    private boolean isTokenExpired(User user) {
        return user.getTokenExpires() == null ||  ( System.currentTimeMillis() / 1000 ) > user.getTokenExpires();
    }

    private boolean isRefreshTokenExpired(User user) {
        return  ( System.currentTimeMillis() / 1000 ) > user.getRefreshTokenExpires();
    }

    @Override
    public void onFindError() {
        authenticationState.setValue(AuthenticationState.UNAUTHENTICATED);
    }

    @Override
    public void onFindSuccess(List<User> items) {
        authenticate(items.get(0));
    }
}

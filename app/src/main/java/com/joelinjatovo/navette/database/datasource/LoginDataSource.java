package com.joelinjatovo.navette.database.datasource;

import androidx.lifecycle.MutableLiveData;

import com.joelinjatovo.navette.data.source.LoginDataSourceBase;
import com.joelinjatovo.navette.ui.auth.login.LoginResult;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource implements LoginDataSourceBase {

    public void login(String phone, String password,  MutableLiveData<LoginResult> resultMutableLiveData) {
        // TODO: handle loggedInUser authentication
        /*
        try {
            User fakeUser = new User();
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
        */
    }

    public void logout() {
        // TODO: revoke authentication
    }
}

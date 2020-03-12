package com.joelinjatovo.navette.database.datasource;

import com.joelinjatovo.navette.data.Result;
import com.joelinjatovo.navette.data.source.LoginDataSourceBase;
import com.joelinjatovo.navette.database.entity.User;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource implements LoginDataSourceBase {

    public Result<User> login(String phone, String password) {
        try {
            // TODO: handle loggedInUser authentication
            User fakeUser = new User();
            return new Result.Success<>(fakeUser);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}

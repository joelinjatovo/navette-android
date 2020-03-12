package com.joelinjatovo.navette.data.repositories;

import com.joelinjatovo.navette.data.source.LoginDataSourceBase;
import com.joelinjatovo.navette.data.Result;
import com.joelinjatovo.navette.database.entity.User;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSourceBase dataSource;

    private User user = null;

    // private constructor : singleton access
    public LoginRepository(LoginDataSourceBase dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSourceBase dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    private void setLoggedInUser(User user) {
        this.user = user;

        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    public Result<User> login(String username, String password) {
        // handle login
        Result<User> result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<User>) result).getData());
        }
        return result;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }
}

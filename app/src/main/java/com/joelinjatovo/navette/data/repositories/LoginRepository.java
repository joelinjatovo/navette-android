package com.joelinjatovo.navette.data.repositories;

import androidx.lifecycle.MutableLiveData;

import com.joelinjatovo.navette.data.source.LoginDataSourceBase;
import com.joelinjatovo.navette.data.Result;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.ui.auth.login.LoginResult;

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

    public void login(String username, String password, MutableLiveData<LoginResult> loginResultMutableLiveData) {
        // handle login
        dataSource.login(username, password, loginResultMutableLiveData);
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }
}

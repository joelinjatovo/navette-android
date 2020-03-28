package com.joelinjatovo.navette.api.repositories;

import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.datasource.LoginDataSourceBase;
import com.joelinjatovo.navette.database.entity.UserWithRoles;

import retrofit2.Callback;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSourceBase dataSource;

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

    public void login(String phone, String password, Callback<RetrofitResponse<UserWithRoles>> callback) {
        dataSource.login(phone, password, callback);
    }

    public void logout() {
        dataSource.logout();
    }
}
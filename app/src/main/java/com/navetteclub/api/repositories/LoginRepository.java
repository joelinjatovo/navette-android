package com.navetteclub.api.repositories;

import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.User;
import com.navetteclub.datasource.LoginDataSourceBase;

import retrofit2.Callback;

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

    public void login(String phone, String password, Callback<RetrofitResponse<User>> callback) {
        dataSource.login(phone, password, callback);
    }

    public void logout() {
        dataSource.logout();
    }
}

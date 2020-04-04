package com.navetteclub.datasource;

import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.User;

import retrofit2.Callback;

public interface LoginDataSourceBase {

    void login(String phone, String password, Callback<RetrofitResponse<User>> callback);

    void logout() ;
}

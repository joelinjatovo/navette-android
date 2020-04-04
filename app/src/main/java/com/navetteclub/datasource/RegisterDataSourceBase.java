package com.navetteclub.datasource;

import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.User;

import retrofit2.Callback;

public interface RegisterDataSourceBase {
    public void register(String name, String phone, String password, Callback<RetrofitResponse<User>> callback);
}

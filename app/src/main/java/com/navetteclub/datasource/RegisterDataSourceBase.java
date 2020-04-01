package com.navetteclub.datasource;

import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.UserWithRoles;

import retrofit2.Callback;

public interface RegisterDataSourceBase {
    public void register(String name, String phone, String password, Callback<RetrofitResponse<UserWithRoles>> callback);
}

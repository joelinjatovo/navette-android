package com.navetteclub.api.repositories;

import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.datasource.RegisterDataSourceBase;
import com.navetteclub.database.entity.UserWithRoles;

import retrofit2.Callback;

public class RegisterRepository {

    private static volatile RegisterRepository instance;

    private RegisterDataSourceBase dataSource;

    // private constructor : singleton access
    public RegisterRepository(RegisterDataSourceBase dataSource) {
        this.dataSource = dataSource;
    }

    public static RegisterRepository getInstance(RegisterDataSourceBase dataSource) {
        if (instance == null) {
            instance = new RegisterRepository(dataSource);
        }
        return instance;
    }

    public void register(String name, String phone, String password, Callback<RetrofitResponse<UserWithRoles>> callback) {
        // handle register
        dataSource.register(name, phone, password, callback);
    }
}
package com.navetteclub.api.repositories;

import com.navetteclub.api.models.Register;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.User;
import com.navetteclub.datasource.RegisterDataSourceBase;

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

    public void register(String name, String phone, String password, Callback<RetrofitResponse<User>> callback) {
        // handle register
        dataSource.register(name, phone, password, callback);
    }

    public void register(Register register, Callback<RetrofitResponse<User>> callback) {
        // handle register
        dataSource.register(register, callback);
    }
}

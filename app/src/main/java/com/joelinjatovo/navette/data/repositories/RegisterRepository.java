package com.joelinjatovo.navette.data.repositories;

import androidx.lifecycle.MutableLiveData;

import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.data.source.RegisterDataSourceBase;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.ui.main.auth.register.RegisterResult;

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
}

package com.joelinjatovo.navette.data.repositories;

import androidx.lifecycle.MutableLiveData;

import com.joelinjatovo.navette.data.source.RegisterDataSourceBase;
import com.joelinjatovo.navette.ui.main.auth.register.RegisterResult;

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

    public void register(String name, String phone, String password, MutableLiveData<RegisterResult> registerResultMutableLiveData) {
        // handle register
        dataSource.register(name, phone, password, registerResultMutableLiveData);
    }
}

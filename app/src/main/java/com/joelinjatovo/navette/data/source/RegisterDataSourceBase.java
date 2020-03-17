package com.joelinjatovo.navette.data.source;

import androidx.lifecycle.MutableLiveData;

import com.joelinjatovo.navette.ui.main.auth.register.RegisterResult;

public interface RegisterDataSourceBase {
    public void register(String name, String phone, String password, MutableLiveData<RegisterResult> resultMutableLiveData);
}

package com.joelinjatovo.navette.data.source;

import androidx.lifecycle.MutableLiveData;

import com.joelinjatovo.navette.ui.main.auth.login.LoginResult;

public interface LoginDataSourceBase {

    public void login(String phone, String password, MutableLiveData<LoginResult> resultMutableLiveData);

    public void logout() ;
}

package com.joelinjatovo.navette.data.source;

import androidx.lifecycle.MutableLiveData;

import com.joelinjatovo.navette.data.Result;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.ui.auth.login.LoginResult;

public interface LoginDataSourceBase {

    public void login(String username, String password, MutableLiveData<LoginResult> resultMutableLiveData);

    public void logout() ;
}

package com.joelinjatovo.navette.data.source;

import androidx.lifecycle.MutableLiveData;

import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.ui.main.auth.register.RegisterResult;

import retrofit2.Callback;

public interface RegisterDataSourceBase {
    public void register(String name, String phone, String password, Callback<RetrofitResponse<User>> callback);
}

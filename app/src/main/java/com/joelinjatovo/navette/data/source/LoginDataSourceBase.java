package com.joelinjatovo.navette.data.source;

import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.database.entity.User;

import retrofit2.Callback;

public interface LoginDataSourceBase {

    void login(String phone, String password, Callback<RetrofitResponse<User>> callback);

    void logout() ;
}

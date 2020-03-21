package com.joelinjatovo.navette.datasource;

import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.database.entity.UserWithRoles;

import retrofit2.Callback;

public interface LoginDataSourceBase {

    void login(String phone, String password, Callback<RetrofitResponse<UserWithRoles>> callback);

    void logout() ;
}

package com.joelinjatovo.navette.data.source;

import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.database.entity.UserWithRoles;

import retrofit2.Callback;

public interface RegisterDataSourceBase {
    public void register(String name, String phone, String password, Callback<RetrofitResponse<UserWithRoles>> callback);
}

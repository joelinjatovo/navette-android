package com.joelinjatovo.navette.api.services;

import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.database.entity.User;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserApiService {
    @GET("/api/v1/user")
    Call<RetrofitResponse<User>> getUser();
}

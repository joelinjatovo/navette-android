package com.joelinjatovo.navette.api.services;

import com.joelinjatovo.navette.api.data.Login;
import com.joelinjatovo.navette.api.data.Register;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.database.entity.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UserApiService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/register")
    Call<RetrofitResponse<User>> register(@Body Register register);

    @GET("api/v1/user")
    Call<RetrofitResponse<User>> getUser();
}

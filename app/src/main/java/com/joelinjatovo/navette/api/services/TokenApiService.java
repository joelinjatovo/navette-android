package com.joelinjatovo.navette.api.services;

import com.joelinjatovo.navette.api.data.Login;
import com.joelinjatovo.navette.database.entity.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TokenApiService {

    @POST("/api/v1/token")
    @Headers("Content-Type: application/json")
    Call<User> getToken(@Body Login login);

    @FormUrlEncoded
    @POST("/api/v1/token/refresh")
    Call<User> refreshToken(@Field("refresh_token") String refreshToken);

    @GET("/api/v1/logout")
    void logout();
}

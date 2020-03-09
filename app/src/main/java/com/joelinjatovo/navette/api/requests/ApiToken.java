package com.joelinjatovo.navette.api.requests;

import com.joelinjatovo.navette.app.entity.Login;
import com.joelinjatovo.navette.app.entity.AccessToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiToken {

    @POST("/api/v1/token")
    @Headers("Content-Type: application/json")
    Call<AccessToken> getToken(@Body Login login);

    @FormUrlEncoded
    @POST("/api/v1/token/refresh")
    Call<AccessToken> refreshToken(@Field("refresh_token") String refreshToken);

    @GET("/api/v1/logout")
    void logout();
}

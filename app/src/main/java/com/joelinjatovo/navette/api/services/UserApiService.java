package com.joelinjatovo.navette.api.services;

import com.joelinjatovo.navette.api.models.Location;
import com.joelinjatovo.navette.api.models.Register;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.database.entity.UserWithRoles;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UserApiService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/register")
    Call<RetrofitResponse<UserWithRoles>> register(@Body Register register);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/user/position")
    Call<RetrofitResponse<UserWithRoles>> addPosition(@Header("Authorization") String token, @Body Location location);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/user")
    Call<RetrofitResponse<UserWithRoles>> getUser();
}

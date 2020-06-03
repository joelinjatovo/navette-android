package com.navetteclub.api.services;

import com.navetteclub.api.models.RidePointParam;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.Ride;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RidePointApiService {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/ridepoint/arrive")
    Call<RetrofitResponse<Ride>> arrive(@Header("Authorization") String token, @Body RidePointParam param);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/ridepoint/cancel")
    Call<RetrofitResponse<Ride>> cancel(@Header("Authorization") String token, @Body RidePointParam param);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/ridepoint/pick-or-drop")
    Call<RetrofitResponse<Ride>> pickOrDrop(@Header("Authorization") String token, @Body RidePointParam param);

}

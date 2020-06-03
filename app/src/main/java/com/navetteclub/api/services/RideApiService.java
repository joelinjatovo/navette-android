package com.navetteclub.api.services;

import com.navetteclub.api.models.RideParam;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.Item;
import com.navetteclub.database.entity.Ride;
import com.navetteclub.database.entity.RidePoint;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RideApiService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/rides")
    Call<RetrofitResponse<List<Ride>>> index(@Header("Authorization") String token, @Query("page") int page);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/ride/{ride}/items")
    Call<RetrofitResponse<List<Item>>> getItems(@Header("Authorization") String token, @Path("ride") Long rideId);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/ride/{ride}/points")
    Call<RetrofitResponse<List<RidePoint>>> getPoints(@Header("Authorization") String token, @Path("ride") Long rideId);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/ride/{ride}")
    Call<RetrofitResponse<Ride>> show(@Header("Authorization") String token, @Path("ride") Long rideId);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/ride/start")
    Call<RetrofitResponse<Ride>> start(@Header("Authorization") String token, @Body RideParam rideParam);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/ride/cancel")
    Call<RetrofitResponse<Ride>> cancel(@Header("Authorization") String token, @Body RideParam rideParam);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/ride/complete")
    Call<RetrofitResponse<Ride>> complete(@Header("Authorization") String token, @Body RideParam rideParam);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/ride/direction")
    Call<RetrofitResponse<Ride>> direction(@Header("Authorization") String token, @Body RideParam rideParam);
}

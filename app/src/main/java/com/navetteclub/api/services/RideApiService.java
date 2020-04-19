package com.navetteclub.api.services;

import com.navetteclub.api.models.OrderRequest;
import com.navetteclub.api.models.RideParam;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.ItemWithDatas;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.RidePointWithDatas;
import com.navetteclub.database.entity.RideWithDatas;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RideApiService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/rides")
    Call<RetrofitResponse<List<RideWithDatas>>> getAll(@Header("Authorization") String token);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/ride/{ride}/items")
    Call<RetrofitResponse<List<ItemWithDatas>>> getItems(@Header("Authorization") String token, @Path("ride") Long rideId);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/ride/{ride}/points")
    Call<RetrofitResponse<List<RidePointWithDatas>>> getPoints(@Header("Authorization") String token, @Path("ride") Long rideId);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/ride/{ride}")
    Call<RetrofitResponse<RideWithDatas>> getRide(@Header("Authorization") String token, @Path("ride") Long rideId);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/ride/start")
    Call<RetrofitResponse<RideWithDatas>> start(@Header("Authorization") String token, @Body RideParam rideParam);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/ride/cancel")
    Call<RetrofitResponse<RideWithDatas>> cancel(@Header("Authorization") String token, @Body RideParam rideParam);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/ride/complete")
    Call<RetrofitResponse<RideWithDatas>> complete(@Header("Authorization") String token, @Body RideParam rideParam);
}

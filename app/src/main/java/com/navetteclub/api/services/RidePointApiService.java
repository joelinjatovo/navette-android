package com.navetteclub.api.services;

import com.navetteclub.api.models.RideParam;
import com.navetteclub.api.models.RidePointParam;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.ItemWithDatas;
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

public interface RidePointApiService {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/ridepoint/finish")
    Call<RetrofitResponse<RideWithDatas>> finish(@Header("Authorization") String token, @Body RidePointParam param);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/ridepoint/cancel")
    Call<RetrofitResponse<RideWithDatas>> cancel(@Header("Authorization") String token, @Body RidePointParam param);

}

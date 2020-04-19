package com.navetteclub.api.services;

import com.navetteclub.api.models.ItemParam;
import com.navetteclub.api.models.RidePointParam;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.RideWithDatas;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ItemApiService {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/item/finish")
    Call<RetrofitResponse<RideWithDatas>> finish(@Header("Authorization") String token, @Body ItemParam param);

}

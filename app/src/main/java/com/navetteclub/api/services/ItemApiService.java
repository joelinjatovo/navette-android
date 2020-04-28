package com.navetteclub.api.services;

import com.navetteclub.api.models.ItemParam;
import com.navetteclub.api.models.RidePointParam;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.ItemWithDatas;
import com.navetteclub.database.entity.RideWithDatas;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ItemApiService {
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/item/{item}")
    Call<RetrofitResponse<ItemWithDatas>> getItem(@Header("Authorization") String token, @Path("item") String itemId);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/item/finish")
    Call<RetrofitResponse<ItemWithDatas>> finish(@Header("Authorization") String token, @Body ItemParam param);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/item/cancel")
    Call<RetrofitResponse<ItemWithDatas>> cancel(@Header("Authorization") String token, @Body ItemParam param);

}

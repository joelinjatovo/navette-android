package com.navetteclub.api.services;

import com.navetteclub.api.models.OrderParam;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.Order;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface CashApiService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/cash/pay")
    Call<RetrofitResponse<Order>> pay(@Header("Authorization") String token, @Body OrderParam orderParam);

}

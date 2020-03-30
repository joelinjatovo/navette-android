package com.joelinjatovo.navette.api.services;

import com.joelinjatovo.navette.api.models.OrderRequest;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.database.entity.Order;
import com.joelinjatovo.navette.database.entity.OrderWithDatas;
import com.joelinjatovo.navette.database.entity.OrderWithPoints;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OrderApiService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/order")
    Call<RetrofitResponse<OrderWithPoints>> createOrder(@Body OrderRequest orderRequest);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/orders")
    Call<RetrofitResponse<List<OrderWithDatas>>> getAll(@Header("Authorization") String token);
}

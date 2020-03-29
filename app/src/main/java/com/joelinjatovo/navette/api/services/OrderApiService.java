package com.joelinjatovo.navette.api.services;

import com.joelinjatovo.navette.api.models.OrderRequest;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.database.entity.Order;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OrderApiService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/order")
    Call<RetrofitResponse<Order>> createOrder(@Body OrderRequest orderRequest);
}

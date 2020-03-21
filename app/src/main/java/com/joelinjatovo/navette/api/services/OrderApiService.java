package com.joelinjatovo.navette.api.services;

import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OrderApiService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/order")
    void createOrder();
}

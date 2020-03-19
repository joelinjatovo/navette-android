package com.joelinjatovo.navette.api.services;

import com.joelinjatovo.navette.api.data.Location;
import com.joelinjatovo.navette.api.data.Register;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.database.entity.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OrderApiService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/order")
    Call<RetrofitResponse<User>> createOrder();
}

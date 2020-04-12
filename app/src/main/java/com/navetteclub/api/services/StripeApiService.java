package com.navetteclub.api.services;

import com.navetteclub.api.models.OrderParam;
import com.navetteclub.api.models.stripe.MyPaymentIntent;
import com.navetteclub.api.responses.RetrofitResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface StripeApiService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/stripe/payment-intent")
    Call<RetrofitResponse<MyPaymentIntent>> createPaymentIntent(@Header("Authorization") String token, @Body OrderParam orderParam);

}

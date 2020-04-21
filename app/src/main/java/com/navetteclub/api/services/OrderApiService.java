package com.navetteclub.api.services;

import com.navetteclub.api.models.OrderParam;
import com.navetteclub.api.models.OrderRequest;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.OrderWithDatas;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderApiService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/order")
    Call<RetrofitResponse<OrderWithDatas>> createOrder(@Header("Authorization") String token, @Body OrderRequest orderRequest);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/order/{order}")
    Call<RetrofitResponse<OrderWithDatas>> getOrder(@Header("Authorization") String token, @Path("order") String orderId);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/cart")
    Call<RetrofitResponse<OrderWithDatas>> getCart(@Body OrderRequest orderRequest);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/orders")
    Call<RetrofitResponse<List<OrderWithDatas>>> getAll(@Header("Authorization") String token, @Query("page") int page);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/order/cancel")
    Call<RetrofitResponse<OrderWithDatas>> cancel(@Header("Authorization") String token, @Body OrderParam orderParam);
}

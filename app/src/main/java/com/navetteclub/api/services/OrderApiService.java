package com.navetteclub.api.services;

import com.navetteclub.api.models.OrderRequest;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.OrderWithPoints;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrderApiService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/club/{club}/order")
    Call<RetrofitResponse<OrderWithDatas>> createOrder(@Header("Authorization") String token, @Path("club") Long clubId, @Body OrderRequest orderRequest);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/order/{order}/pay/{type}")
    Call<RetrofitResponse<OrderWithDatas>> confirmPayment(@Header("Authorization") String token, @Path("order") String rid, @Path("type") String paymentType);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/orders")
    Call<RetrofitResponse<List<OrderWithDatas>>> getAll(@Header("Authorization") String token);
}

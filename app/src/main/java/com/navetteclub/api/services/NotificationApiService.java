package com.navetteclub.api.services;

import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.Notification;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NotificationApiService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/notifications")
    Call<RetrofitResponse<List<Notification>>> getAll(@Header("Authorization") String token, @Query("page") int page);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/notifications")
    Call<RetrofitResponse<List<Notification>>> markAllAsRead(@Header("Authorization") String token);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/notifications/unread")
    Call<RetrofitResponse<List<Notification>>> getAllUnread(@Header("Authorization") String token);
}

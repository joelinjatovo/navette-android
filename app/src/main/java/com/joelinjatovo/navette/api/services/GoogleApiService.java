package com.joelinjatovo.navette.api.services;

import com.joelinjatovo.navette.api.data.google.GoogleDirectionResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleApiService {
    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("api/directions/json")
    Call<GoogleDirectionResponse> getDirection(@Query("key") String key, @Query("units") String units, @Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode);
}

package com.joelinjatovo.navette.api.services;

import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.database.entity.CarAndModel;
import com.joelinjatovo.navette.database.entity.ClubAndPoint;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ClubApiService {
    
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/clubs")
    Call<RetrofitResponse<List<ClubAndPoint>>> getClubs();

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/club/{club}/cars")
    Call<RetrofitResponse<List<CarAndModel>>> getCars(@Query("club") Integer clubId);
}

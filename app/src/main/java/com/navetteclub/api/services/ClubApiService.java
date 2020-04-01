package com.navetteclub.api.services;

import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.CarAndModel;
import com.navetteclub.database.entity.ClubAndPoint;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface ClubApiService {
    
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/clubs")
    Call<RetrofitResponse<List<ClubAndPoint>>> getClubs();

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/club/{club}/cars")
    Call<RetrofitResponse<List<CarAndModel>>> getCars(@Path("club") Long clubId);
}

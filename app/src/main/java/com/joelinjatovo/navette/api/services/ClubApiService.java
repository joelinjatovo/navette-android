package com.joelinjatovo.navette.api.services;

import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.database.entity.Club;
import com.joelinjatovo.navette.database.entity.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ClubApiService {
    
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/clubs")
    Call<RetrofitResponse<Club>> getClubs();
}

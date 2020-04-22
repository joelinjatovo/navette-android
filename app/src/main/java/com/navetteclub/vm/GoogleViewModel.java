package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.navetteclub.api.models.google.GoogleDirectionResponse;
import com.navetteclub.api.models.google.Leg;
import com.navetteclub.api.models.google.Route;
import com.navetteclub.api.services.GoogleApiService;
import com.navetteclub.ui.order.OrderFragment;
import com.navetteclub.utils.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleViewModel extends ViewModel {

    private static final String TAG = GoogleViewModel.class.getSimpleName();

    private MutableLiveData<String> error1Result;
    private MutableLiveData<String> error2Result;

    private MutableLiveData<Response<GoogleDirectionResponse>> direction1Result;
    private MutableLiveData<Response<GoogleDirectionResponse>> direction2Result;

    public GoogleViewModel() {
        direction1Result = new MutableLiveData<>();
        direction2Result = new MutableLiveData<>();
        error1Result = new MutableLiveData<>();
        error2Result = new MutableLiveData<>();
    }

    public void loadDirection1(String apiKey, LatLng origin, LatLng destination, String waypoints) {
        loadDirection(apiKey, origin, destination, waypoints, true);
    }

    public void loadDirection2(String apiKey, LatLng origin, LatLng destination, String waypoints) {
        loadDirection(apiKey, origin, destination, waypoints, false);
    }


    public void loadDirection(String apiKey, LatLng origin, LatLng destination, String waypoints, boolean isMain) {
        String url = "https://maps.googleapis.com/maps/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GoogleApiService service = retrofit.create(GoogleApiService.class);
        Call<GoogleDirectionResponse> call;
        if(waypoints==null){

            call = service.getDirection(
                    apiKey,
                    "metric",
                    origin.latitude + "," + origin.longitude,
                    destination.latitude + "," + destination.longitude,
                    "driving"
            );
        }else{
            call = service.getDirection(
                    apiKey,
                    "metric",
                    origin.latitude + "," + origin.longitude,
                    destination.latitude + "," + destination.longitude,
                    "driving",
                    waypoints
            );
        }

        call.enqueue(new Callback<GoogleDirectionResponse>() {
            @Override
            public void onResponse(@NonNull Call<GoogleDirectionResponse> call,
                                   @NonNull Response<GoogleDirectionResponse> response) {
                if(response.isSuccessful()){
                    if(isMain)
                        direction1Result.setValue(response);
                    else
                        direction2Result.setValue(response);
                }else{
                    if(isMain) {
                        direction1Result.setValue(null);
                        error1Result.setValue(response.message());
                    }else{
                        direction2Result.setValue(null);
                        error2Result.setValue(response.message());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GoogleDirectionResponse> call,
                                  @NonNull Throwable t) {
                Log.e(TAG, t.toString(), t);
                if(isMain) {
                    direction1Result.setValue(null);
                    error1Result.setValue(t.getMessage());
                }else{
                    direction2Result.setValue(null);
                    error2Result.setValue(t.getMessage());
                }
            }
        });
    }

    public MutableLiveData<Response<GoogleDirectionResponse>> getDirection1Result() {
        return direction1Result;
    }

    public MutableLiveData<Response<GoogleDirectionResponse>> getDirection2Result() {
        return direction2Result;
    }

    public void setDirection1Result(Response<GoogleDirectionResponse> directionResult) {
        this.direction1Result.setValue(directionResult);
    }

    public void setDirection2Result(Response<GoogleDirectionResponse> directionResult) {
        this.direction2Result.setValue(directionResult);
    }

    public MutableLiveData<String> getError1Result() {
        return error1Result;
    }

    public void setError1Result(String error) {
        this.error1Result.setValue(error);
    }

    public MutableLiveData<String> getError2Result() {
        return error2Result;
    }

    public void setError2Result(String error) {
        this.error2Result.setValue(error);
    }

    public void refresh() {
        error1Result.setValue(null);
        direction1Result.setValue(null);
        error2Result.setValue(null);
        direction2Result.setValue(null);
    }
}
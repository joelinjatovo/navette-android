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

    private MutableLiveData<String> errorResult;

    private MutableLiveData<Response<GoogleDirectionResponse>> directionResult;

    public GoogleViewModel() {
        directionResult = new MutableLiveData<>();
        errorResult = new MutableLiveData<>();
    }

    public void loadDirection(String apiKey, LatLng origin, LatLng destination) {
        String url = "https://maps.googleapis.com/maps/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GoogleApiService service = retrofit.create(GoogleApiService.class);
        Call<GoogleDirectionResponse> call = service.getDirection(
                apiKey,
                "metric",
                origin.latitude + "," + origin.longitude,
                destination.latitude + "," + destination.longitude,
                "driving"
        );

        call.enqueue(new Callback<GoogleDirectionResponse>() {
            @Override
            public void onResponse(@NonNull Call<GoogleDirectionResponse> call,
                                   @NonNull Response<GoogleDirectionResponse> response) {
                if(response.isSuccessful()){
                    directionResult.setValue(response);
                }else{
                    directionResult.setValue(null);
                    errorResult.setValue(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<GoogleDirectionResponse> call,
                                  @NonNull Throwable t) {
                Log.e(TAG, t.toString(), t);
                directionResult.setValue(null);
                errorResult.setValue(t.getMessage());
            }
        });
    }

    public void loadDirection(String apiKey, LatLng origin, LatLng destination, String waypoints) {
        String url = "https://maps.googleapis.com/maps/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GoogleApiService service = retrofit.create(GoogleApiService.class);
        Call<GoogleDirectionResponse> call = service.getDirection(
                apiKey,
                "metric",
                origin.latitude + "," + origin.longitude,
                destination.latitude + "," + destination.longitude,
                "driving",
                waypoints
        );

        call.enqueue(new Callback<GoogleDirectionResponse>() {
            @Override
            public void onResponse(@NonNull Call<GoogleDirectionResponse> call,
                                   @NonNull Response<GoogleDirectionResponse> response) {
                if(response.isSuccessful()){
                    directionResult.setValue(response);
                }else{
                    directionResult.setValue(null);
                    errorResult.setValue(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<GoogleDirectionResponse> call,
                                  @NonNull Throwable t) {
                Log.e(TAG, t.toString(), t);
                directionResult.setValue(null);
                errorResult.setValue(t.getMessage());
            }
        });
    }

    public MutableLiveData<Response<GoogleDirectionResponse>> getDirectionResult() {
        return directionResult;
    }

    public void setDirectionResult(MutableLiveData<Response<GoogleDirectionResponse>> directionResult) {
        this.directionResult = directionResult;
    }

    public MutableLiveData<String> getErrorResult() {
        return errorResult;
    }

    public void setErrorResult(String error) {
        this.errorResult.setValue(error);
    }
}
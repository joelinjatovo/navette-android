package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.RideParam;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.OrderApiService;
import com.navetteclub.api.services.RideApiService;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.Ride;
import com.navetteclub.database.entity.RidePointWithDatas;
import com.navetteclub.database.entity.RideWithDatas;
import com.navetteclub.database.entity.User;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RidesViewModel extends ViewModel {

    private static final String TAG = RidesViewModel.class.getSimpleName();

    private RideWithDatas rideWithDatas;

    private MutableLiveData<RemoteLoaderResult<RideWithDatas>> rideLiveData = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<List<RideWithDatas>>> ridesLiveData = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<List<OrderWithDatas>>> ordersLiveData = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<List<RidePointWithDatas>>> pointsLiveData = new MutableLiveData<>();

    public RideWithDatas getRideWithDatas() {
        return rideWithDatas;
    }

    public void setRideWithDatas(RideWithDatas rideWithDatas) {
        this.rideWithDatas = rideWithDatas;
    }

    public void setRidesLiveData(RemoteLoaderResult<List<RideWithDatas>> result) {
        ridesLiveData.setValue(result);
    }

    public MutableLiveData<RemoteLoaderResult<List<RideWithDatas>>> getRidesLiveData() {
        return ridesLiveData;
    }

    public MutableLiveData<RemoteLoaderResult<List<OrderWithDatas>>> getOrdersLiveData() {
        return ordersLiveData;
    }

    public void setOrdersLiveData(RemoteLoaderResult<List<OrderWithDatas>> ordersLiveData) {
        this.ordersLiveData.setValue(ordersLiveData);
    }

    public MutableLiveData<RemoteLoaderResult<List<RidePointWithDatas>>> getPointsLiveData() {
        return pointsLiveData;
    }

    public void setPointsLiveData(RemoteLoaderResult<List<RidePointWithDatas>> pointsLiveData) {
        this.pointsLiveData.setValue(pointsLiveData);
    }

    public void start(String token, Long rideId){
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<RideWithDatas>> call = service.start(token, new RideParam(rideId));
        call.enqueue(new Callback<RetrofitResponse<RideWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<RideWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<RideWithDatas>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        rideLiveData.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        rideLiveData.setValue(new RemoteLoaderResult<>(response.body().getStatusResString()));
                    }
                }else{
                    rideLiveData.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<RideWithDatas>> call,
                                  @NonNull Throwable throwable) {
                rideLiveData.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
            }
        });
    }

    public void load(User user){
        Log.d(TAG, "RidesApiService.getAll( " +  user.getAuthorizationToken() + ")");
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<List<RideWithDatas>>> call = service.getAll(user.getAuthorizationToken());
        call.enqueue(new Callback<RetrofitResponse<List<RideWithDatas>>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<List<RideWithDatas>>> call,
                                   @NonNull Response<RetrofitResponse<List<RideWithDatas>>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        ridesLiveData.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        ridesLiveData.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }
                }else{
                    ridesLiveData.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<List<RideWithDatas>>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                ridesLiveData.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void loadOrders(User user, Ride ride){
        Log.d(TAG, "OrderApiService.loadOrders( " +  user.getAuthorizationToken() + ")");
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<List<OrderWithDatas>>> call = service.getOrders(user.getAuthorizationToken(), ride.getId());
        call.enqueue(new Callback<RetrofitResponse<List<OrderWithDatas>>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<List<OrderWithDatas>>> call,
                                   @NonNull Response<RetrofitResponse<List<OrderWithDatas>>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        ordersLiveData.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        ordersLiveData.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }
                }else{
                    ordersLiveData.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<List<OrderWithDatas>>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                ordersLiveData.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void loadPoints(String token, Long rideID){
        Log.d(TAG, "OrderApiService.loadPoints( " +  rideID + ")");
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<List<RidePointWithDatas>>> call = service.getPoints(token,rideID);
        call.enqueue(new Callback<RetrofitResponse<List<RidePointWithDatas>>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<List<RidePointWithDatas>>> call,
                                   @NonNull Response<RetrofitResponse<List<RidePointWithDatas>>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        pointsLiveData.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        pointsLiveData.setValue(new RemoteLoaderResult<>(response.body().getStatusResString()));
                    }
                }else{
                    pointsLiveData.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<List<RidePointWithDatas>>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                pointsLiveData.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public MutableLiveData<RemoteLoaderResult<RideWithDatas>> getRideLiveData() {
        return rideLiveData;
    }

    public void setRideLiveData(RemoteLoaderResult<RideWithDatas> rideLiveData) {
        this.rideLiveData.setValue(rideLiveData);
    }
}

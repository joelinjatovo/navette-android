package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.OrderApiService;
import com.navetteclub.api.services.RideApiService;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.Ride;
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

    public void start(User user, Ride ride){
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<RideWithDatas>> call = service.start(user.getAuthorizationToken(), ride.getId());
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
                        switch (response.body().getStatus()){
                            case 400:
                                ridesLiveData.setValue(new RemoteLoaderResult<>(R.string.error_400));
                                break;
                            case 401:
                                ridesLiveData.setValue(new RemoteLoaderResult<>(R.string.error_401));
                                break;
                            case 402:
                                ridesLiveData.setValue(new RemoteLoaderResult<>(R.string.error_402));
                                break;
                            case 403:
                                ridesLiveData.setValue(new RemoteLoaderResult<>(R.string.error_403));
                                break;
                            case 404:
                                ridesLiveData.setValue(new RemoteLoaderResult<>(R.string.error_404));
                                break;
                            default:
                                ridesLiveData.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                                break;
                        }
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
        Log.d(TAG, "OrderApiService.getAll( " +  user.getAuthorizationToken() + ")");
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
                        switch (response.body().getStatus()){
                            case 400:
                                ordersLiveData.setValue(new RemoteLoaderResult<>(R.string.error_400));
                                break;
                            case 401:
                                ordersLiveData.setValue(new RemoteLoaderResult<>(R.string.error_401));
                                break;
                            case 402:
                                ordersLiveData.setValue(new RemoteLoaderResult<>(R.string.error_402));
                                break;
                            case 403:
                                ordersLiveData.setValue(new RemoteLoaderResult<>(R.string.error_403));
                                break;
                            case 404:
                                ordersLiveData.setValue(new RemoteLoaderResult<>(R.string.error_404));
                                break;
                            default:
                                ordersLiveData.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                                break;
                        }
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

    public MutableLiveData<RemoteLoaderResult<RideWithDatas>> getRideLiveData() {
        return rideLiveData;
    }

    public void setRideLiveData(MutableLiveData<RemoteLoaderResult<RideWithDatas>> rideLiveData) {
        this.rideLiveData = rideLiveData;
    }
}

package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.RideParam;
import com.navetteclub.api.models.RidePointParam;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.ItemApiService;
import com.navetteclub.api.services.RideApiService;
import com.navetteclub.api.services.RidePointApiService;
import com.navetteclub.database.entity.ItemWithDatas;
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

public class RideViewModel extends ViewModel {

    private static final String TAG = RideViewModel.class.getSimpleName();

    private MutableLiveData<RemoteLoaderResult<ItemWithDatas>> itemViewResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<RideWithDatas>> rideFinishResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<RideWithDatas>> rideArrivedResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<RideWithDatas>> rideCancelResult = new MutableLiveData<>();

    public void loadItem(String token, String itemId) {
        ItemApiService service = RetrofitClient.getInstance().create(ItemApiService.class);
        Call<RetrofitResponse<ItemWithDatas>> call = service.getItem(token, itemId);
        call.enqueue(new Callback<RetrofitResponse<ItemWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<ItemWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<ItemWithDatas>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        itemViewResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        itemViewResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    itemViewResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<ItemWithDatas>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                itemViewResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void finishRidePoint(String token, String ridePointId){
        RidePointApiService service = RetrofitClient.getInstance().create(RidePointApiService.class);
        Call<RetrofitResponse<RideWithDatas>> call = service.finish(token, new RidePointParam(ridePointId));
        call.enqueue(new Callback<RetrofitResponse<RideWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<RideWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<RideWithDatas>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        rideFinishResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        rideFinishResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    rideFinishResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<RideWithDatas>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                rideFinishResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void arriveRidePoint(String token, String ridePoindId){
        RidePointApiService service = RetrofitClient.getInstance().create(RidePointApiService.class);
        Call<RetrofitResponse<RideWithDatas>> call = service.arrive(token, new RidePointParam(ridePoindId));
        call.enqueue(new Callback<RetrofitResponse<RideWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<RideWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<RideWithDatas>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        rideArrivedResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        rideArrivedResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    rideArrivedResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<RideWithDatas>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                rideArrivedResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void cancelRidePoint(String token, String ridePoindId){
        RidePointApiService service = RetrofitClient.getInstance().create(RidePointApiService.class);
        Call<RetrofitResponse<RideWithDatas>> call = service.cancel(token, new RidePointParam(ridePoindId));
        call.enqueue(new Callback<RetrofitResponse<RideWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<RideWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<RideWithDatas>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        rideCancelResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        rideCancelResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    rideCancelResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<RideWithDatas>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                rideCancelResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public LiveData<RemoteLoaderResult<RideWithDatas>> getRideFinishResult() {
        return rideFinishResult;
    }

    public void setRideFinishResult(RemoteLoaderResult<RideWithDatas> rideResult) {
        this.rideFinishResult.setValue(rideResult);
    }

    public LiveData<RemoteLoaderResult<RideWithDatas>> getRideCancelResult() {
        return rideCancelResult;
    }

    public void setRideCancelResult(RemoteLoaderResult<RideWithDatas> rideCancelResult) {
        this.rideCancelResult.setValue(rideCancelResult);
    }

    public MutableLiveData<RemoteLoaderResult<ItemWithDatas>> getItemViewResult() {
        return itemViewResult;
    }

    public void setItemViewResult(RemoteLoaderResult<ItemWithDatas> itemViewResult) {
        this.itemViewResult.setValue(itemViewResult);
    }

    public MutableLiveData<RemoteLoaderResult<RideWithDatas>> getRideArrivedResult() {
        return rideArrivedResult;
    }

    public void setRideArrivedResult(RemoteLoaderResult<RideWithDatas> rideArrivedResult) {
        this.rideArrivedResult.setValue(rideArrivedResult);
    }
}

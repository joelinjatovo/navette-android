package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.RidePointParam;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.ItemApiService;
import com.navetteclub.api.services.RidePointApiService;
import com.navetteclub.database.entity.Item;
import com.navetteclub.database.entity.Ride;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideViewModel extends ViewModel {

    private static final String TAG = RideViewModel.class.getSimpleName();

    private MutableLiveData<RemoteLoaderResult<Item>> itemViewResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<Ride>> ridePickOrDropResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<Ride>> rideArrivedResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<Ride>> rideCancelResult = new MutableLiveData<>();

    public void loadItem(String token, String itemId) {
        ItemApiService service = RetrofitClient.getInstance().create(ItemApiService.class);
        Call<RetrofitResponse<Item>> call = service.show(token, itemId);
        call.enqueue(new Callback<RetrofitResponse<Item>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<Item>> call,
                                   @NonNull Response<RetrofitResponse<Item>> response) {
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
            public void onFailure(@NonNull Call<RetrofitResponse<Item>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                itemViewResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void pickOrDrop(String token, String ridePointId){
        RidePointApiService service = RetrofitClient.getInstance().create(RidePointApiService.class);
        Call<RetrofitResponse<Ride>> call = service.pickOrDrop(token, new RidePointParam(ridePointId));
        call.enqueue(new Callback<RetrofitResponse<Ride>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<Ride>> call,
                                   @NonNull Response<RetrofitResponse<Ride>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        ridePickOrDropResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        ridePickOrDropResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    ridePickOrDropResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<Ride>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                ridePickOrDropResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void arriveRidePoint(String token, String ridePoindId){
        RidePointApiService service = RetrofitClient.getInstance().create(RidePointApiService.class);
        Call<RetrofitResponse<Ride>> call = service.arrive(token, new RidePointParam(ridePoindId));
        call.enqueue(new Callback<RetrofitResponse<Ride>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<Ride>> call,
                                   @NonNull Response<RetrofitResponse<Ride>> response) {
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
            public void onFailure(@NonNull Call<RetrofitResponse<Ride>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                rideArrivedResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void cancelRidePoint(String token, String ridePoindId){
        RidePointApiService service = RetrofitClient.getInstance().create(RidePointApiService.class);
        Call<RetrofitResponse<Ride>> call = service.cancel(token, new RidePointParam(ridePoindId));
        call.enqueue(new Callback<RetrofitResponse<Ride>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<Ride>> call,
                                   @NonNull Response<RetrofitResponse<Ride>> response) {
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
            public void onFailure(@NonNull Call<RetrofitResponse<Ride>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                rideCancelResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public LiveData<RemoteLoaderResult<Ride>> getRidePickOrDropResult() {
        return ridePickOrDropResult;
    }

    public void setRidePickOrDropResult(RemoteLoaderResult<Ride> rideResult) {
        this.ridePickOrDropResult.setValue(rideResult);
    }

    public LiveData<RemoteLoaderResult<Ride>> getRideCancelResult() {
        return rideCancelResult;
    }

    public void setRideCancelResult(RemoteLoaderResult<Ride> rideCancelResult) {
        this.rideCancelResult.setValue(rideCancelResult);
    }

    public MutableLiveData<RemoteLoaderResult<Item>> getItemViewResult() {
        return itemViewResult;
    }

    public void setItemViewResult(RemoteLoaderResult<Item> itemViewResult) {
        this.itemViewResult.setValue(itemViewResult);
    }

    public MutableLiveData<RemoteLoaderResult<Ride>> getRideArrivedResult() {
        return rideArrivedResult;
    }

    public void setRideArrivedResult(RemoteLoaderResult<Ride> rideArrivedResult) {
        this.rideArrivedResult.setValue(rideArrivedResult);
    }
}

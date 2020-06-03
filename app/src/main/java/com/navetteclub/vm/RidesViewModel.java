package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.Pagination;
import com.navetteclub.api.models.RideParam;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.RideApiService;
import com.navetteclub.database.entity.Item;
import com.navetteclub.database.entity.Ride;
import com.navetteclub.database.entity.RidePoint;
import com.navetteclub.database.entity.User;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RidesViewModel extends ViewModel {

    private static final String TAG = RidesViewModel.class.getSimpleName();

    private Ride rideWithDatas;

    private MutableLiveData<Ride> rideLiveData = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<List<Ride>>> ridesResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<Ride>> rideResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<Ride>> rideDirectionResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<Ride>> rideStartResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<Ride>> rideCancelResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<Ride>> rideCompleteResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<List<Item>>> itemsResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<List<RidePoint>>> pointsResult = new MutableLiveData<>();

    private MutableLiveData<Pagination> paginationResult = new MutableLiveData<>();

    public void setRidesResult(RemoteLoaderResult<List<Ride>> result) {
        ridesResult.setValue(result);
    }

    public MutableLiveData<RemoteLoaderResult<List<Ride>>> getRidesResult() {
        return ridesResult;
    }

    public MutableLiveData<RemoteLoaderResult<List<Item>>> getItemsResult() {
        return itemsResult;
    }

    public void setItemsResult(RemoteLoaderResult<List<Item>> itemsResult) {
        this.itemsResult.setValue(itemsResult);
    }

    public LiveData<RemoteLoaderResult<List<RidePoint>>> getPointsResult() {
        return pointsResult;
    }

    public void setPointsResult(RemoteLoaderResult<List<RidePoint>> pointsResult) {
        this.pointsResult.setValue(pointsResult);
    }

    public MutableLiveData<Pagination> getPaginationResult() {
        return paginationResult;
    }

    public void setPaginationResult(Pagination paginationResult) {
        this.paginationResult.setValue(paginationResult);
    }

    public void direction(String token, Long rideId){
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<Ride>> call = service.direction(token, new RideParam(rideId));
        call.enqueue(new Callback<RetrofitResponse<Ride>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<Ride>> call,
                                   @NonNull Response<RetrofitResponse<Ride>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        paginationResult.setValue(response.body().getPagination());
                        rideDirectionResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        rideDirectionResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    rideDirectionResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<Ride>> call,
                                  @NonNull Throwable throwable) {
                rideDirectionResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
            }
        });
    }

    public void active(String token, Long rideId){
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<Ride>> call = service.start(token, new RideParam(rideId));
        call.enqueue(new Callback<RetrofitResponse<Ride>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<Ride>> call,
                                   @NonNull Response<RetrofitResponse<Ride>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        rideStartResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        rideStartResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    rideStartResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<Ride>> call,
                                  @NonNull Throwable throwable) {
                rideStartResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
            }
        });
    }

    public void cancel(String token, Long rideId){
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<Ride>> call = service.cancel(token, new RideParam(rideId));
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
                rideCancelResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
            }
        });
    }

    public void complete(String token, Long rideId){
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<Ride>> call = service.complete(token, new RideParam(rideId));
        call.enqueue(new Callback<RetrofitResponse<Ride>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<Ride>> call,
                                   @NonNull Response<RetrofitResponse<Ride>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        rideCompleteResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        rideCompleteResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    rideCompleteResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<Ride>> call,
                                  @NonNull Throwable throwable) {
                rideCompleteResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
            }
        });
    }

    public void load(User user, int page){
        Log.d(TAG, "RidesApiService.getAll( " +  user.getAuthorizationToken() + ")");
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<List<Ride>>> call = service.index(user.getAuthorizationToken(), page);
        call.enqueue(new Callback<RetrofitResponse<List<Ride>>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<List<Ride>>> call,
                                   @NonNull Response<RetrofitResponse<List<Ride>>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        ridesResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        ridesResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    ridesResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<List<Ride>>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                ridesResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void loadItems(User user, Ride ride){
        Log.d(TAG, "OrderApiService.loadOrders( " +  user.getAuthorizationToken() + ")");
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<List<Item>>> call = service.getItems(user.getAuthorizationToken(), ride.getId());
        call.enqueue(new Callback<RetrofitResponse<List<Item>>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<List<Item>>> call,
                                   @NonNull Response<RetrofitResponse<List<Item>>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        itemsResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        itemsResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    itemsResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<List<Item>>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                itemsResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void loadPoints(String token, Long rideID){
        Log.d(TAG, "OrderApiService.loadPoints( " +  rideID + ")");
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<List<RidePoint>>> call = service.getPoints(token,rideID);
        call.enqueue(new Callback<RetrofitResponse<List<RidePoint>>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<List<RidePoint>>> call,
                                   @NonNull Response<RetrofitResponse<List<RidePoint>>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        pointsResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        pointsResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    pointsResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<List<RidePoint>>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                pointsResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void loadRide(String token, Long rideId) {
        Log.d(TAG, "OrderApiService.loadRide( " +  rideId + ")");
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<Ride>> call = service.show(token,rideId);
        call.enqueue(new Callback<RetrofitResponse<Ride>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<Ride>> call,
                                   @NonNull Response<RetrofitResponse<Ride>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        rideResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        rideResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    rideResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<Ride>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                rideResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });

    }

    public MutableLiveData<RemoteLoaderResult<Ride>> getRideStartResult() {
        return rideStartResult;
    }

    public void setRideStartResult(RemoteLoaderResult<Ride> rideLiveData) {
        this.rideStartResult.setValue(rideLiveData);
    }

    public LiveData<Ride> getRideLiveData() {
        return rideLiveData;
    }

    public void setRideLiveData(Ride rideLiveData) {
        this.rideWithDatas = rideLiveData;
        this.rideLiveData.setValue(rideLiveData);
    }

    public Ride getRide() {
        return rideWithDatas;
    }

    public LiveData<RemoteLoaderResult<Ride>> getRideCancelResult() {
        return rideCancelResult;
    }

    public void setRideCancelResult(RemoteLoaderResult<Ride> rideCancelResult) {
        this.rideCancelResult.setValue(rideCancelResult);
    }

    public LiveData<RemoteLoaderResult<Ride>> getRideResult() {
        return rideResult;
    }

    public void setRideResult(RemoteLoaderResult<Ride> rideResult) {
        this.rideResult.setValue(rideResult);
    }

    public LiveData<RemoteLoaderResult<Ride>> getRideDirectionResult() {
        return rideDirectionResult;
    }

    public void setRideDirectionResult(RemoteLoaderResult<Ride> directionResult) {
        this.rideDirectionResult.setValue(directionResult);
    }

    public LiveData<RemoteLoaderResult<Ride>> getRideCompleteResult() {
        return rideCompleteResult;
    }

    public void setRideCompleteResult(RemoteLoaderResult<Ride> rideCompleteResult) {
        this.rideCompleteResult.setValue(rideCompleteResult);
    }
}

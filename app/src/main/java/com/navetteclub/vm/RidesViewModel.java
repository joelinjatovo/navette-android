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
import com.navetteclub.api.services.OrderApiService;
import com.navetteclub.api.services.RideApiService;
import com.navetteclub.database.entity.ItemWithDatas;
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

    private MutableLiveData<RideWithDatas> rideLiveData = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<List<RideWithDatas>>> ridesResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<RideWithDatas>> rideResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<RideWithDatas>> rideDirectionResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<RideWithDatas>> rideStartResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<RideWithDatas>> rideCancelResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<RideWithDatas>> rideCompleteResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<List<ItemWithDatas>>> itemsResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<List<RidePointWithDatas>>> pointsResult = new MutableLiveData<>();

    private MutableLiveData<Pagination> paginationResult = new MutableLiveData<>();

    public void setRidesResult(RemoteLoaderResult<List<RideWithDatas>> result) {
        ridesResult.setValue(result);
    }

    public MutableLiveData<RemoteLoaderResult<List<RideWithDatas>>> getRidesResult() {
        return ridesResult;
    }

    public MutableLiveData<RemoteLoaderResult<List<ItemWithDatas>>> getItemsResult() {
        return itemsResult;
    }

    public void setItemsResult(RemoteLoaderResult<List<ItemWithDatas>> itemsResult) {
        this.itemsResult.setValue(itemsResult);
    }

    public LiveData<RemoteLoaderResult<List<RidePointWithDatas>>> getPointsResult() {
        return pointsResult;
    }

    public void setPointsResult(RemoteLoaderResult<List<RidePointWithDatas>> pointsResult) {
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
        Call<RetrofitResponse<RideWithDatas>> call = service.direction(token, new RideParam(rideId));
        call.enqueue(new Callback<RetrofitResponse<RideWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<RideWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<RideWithDatas>> response) {
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
            public void onFailure(@NonNull Call<RetrofitResponse<RideWithDatas>> call,
                                  @NonNull Throwable throwable) {
                rideDirectionResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
            }
        });
    }

    public void active(String token, Long rideId){
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<RideWithDatas>> call = service.active(token, new RideParam(rideId));
        call.enqueue(new Callback<RetrofitResponse<RideWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<RideWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<RideWithDatas>> response) {
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
            public void onFailure(@NonNull Call<RetrofitResponse<RideWithDatas>> call,
                                  @NonNull Throwable throwable) {
                rideStartResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
            }
        });
    }

    public void cancel(String token, Long rideId){
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<RideWithDatas>> call = service.cancel(token, new RideParam(rideId));
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
                rideCancelResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
            }
        });
    }

    public void complete(String token, Long rideId){
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<RideWithDatas>> call = service.complete(token, new RideParam(rideId));
        call.enqueue(new Callback<RetrofitResponse<RideWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<RideWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<RideWithDatas>> response) {
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
            public void onFailure(@NonNull Call<RetrofitResponse<RideWithDatas>> call,
                                  @NonNull Throwable throwable) {
                rideCompleteResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
            }
        });
    }

    public void load(User user, int page){
        Log.d(TAG, "RidesApiService.getAll( " +  user.getAuthorizationToken() + ")");
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<List<RideWithDatas>>> call = service.getAll(user.getAuthorizationToken(), page);
        call.enqueue(new Callback<RetrofitResponse<List<RideWithDatas>>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<List<RideWithDatas>>> call,
                                   @NonNull Response<RetrofitResponse<List<RideWithDatas>>> response) {
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
            public void onFailure(@NonNull Call<RetrofitResponse<List<RideWithDatas>>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                ridesResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void loadItems(User user, Ride ride){
        Log.d(TAG, "OrderApiService.loadOrders( " +  user.getAuthorizationToken() + ")");
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<List<ItemWithDatas>>> call = service.getItems(user.getAuthorizationToken(), ride.getId());
        call.enqueue(new Callback<RetrofitResponse<List<ItemWithDatas>>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<List<ItemWithDatas>>> call,
                                   @NonNull Response<RetrofitResponse<List<ItemWithDatas>>> response) {
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
            public void onFailure(@NonNull Call<RetrofitResponse<List<ItemWithDatas>>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                itemsResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
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
                        pointsResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        pointsResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    pointsResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<List<RidePointWithDatas>>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                pointsResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void loadRide(String token, Long rideId) {
        Log.d(TAG, "OrderApiService.loadRide( " +  rideId + ")");
        RideApiService service = RetrofitClient.getInstance().create(RideApiService.class);
        Call<RetrofitResponse<RideWithDatas>> call = service.getRide(token,rideId);
        call.enqueue(new Callback<RetrofitResponse<RideWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<RideWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<RideWithDatas>> response) {
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
            public void onFailure(@NonNull Call<RetrofitResponse<RideWithDatas>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                rideResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });

    }

    public MutableLiveData<RemoteLoaderResult<RideWithDatas>> getRideStartResult() {
        return rideStartResult;
    }

    public void setRideStartResult(RemoteLoaderResult<RideWithDatas> rideLiveData) {
        this.rideStartResult.setValue(rideLiveData);
    }

    public LiveData<RideWithDatas> getRideLiveData() {
        return rideLiveData;
    }

    public void setRideLiveData(RideWithDatas rideLiveData) {
        this.rideWithDatas = rideLiveData;
        this.rideLiveData.setValue(rideLiveData);
    }

    public RideWithDatas getRide() {
        return rideWithDatas;
    }

    public LiveData<RemoteLoaderResult<RideWithDatas>> getRideCancelResult() {
        return rideCancelResult;
    }

    public void setRideCancelResult(RemoteLoaderResult<RideWithDatas> rideCancelResult) {
        this.rideCancelResult.setValue(rideCancelResult);
    }

    public LiveData<RemoteLoaderResult<RideWithDatas>> getRideResult() {
        return rideResult;
    }

    public void setRideResult(RemoteLoaderResult<RideWithDatas> rideResult) {
        this.rideResult.setValue(rideResult);
    }

    public LiveData<RemoteLoaderResult<RideWithDatas>> getRideDirectionResult() {
        return rideDirectionResult;
    }

    public void setRideDirectionResult(RemoteLoaderResult<RideWithDatas> directionResult) {
        this.rideDirectionResult.setValue(directionResult);
    }

    public LiveData<RemoteLoaderResult<RideWithDatas>> getRideCompleteResult() {
        return rideCompleteResult;
    }

    public void setRideCompleteResult(RemoteLoaderResult<RideWithDatas> rideCompleteResult) {
        this.rideCompleteResult.setValue(rideCompleteResult);
    }
}

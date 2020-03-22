package com.joelinjatovo.navette.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.api.clients.RetrofitClient;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.api.services.ClubApiService;
import com.joelinjatovo.navette.database.entity.CarAndModel;
import com.joelinjatovo.navette.database.entity.Club;
import com.joelinjatovo.navette.database.entity.ClubAndPoint;
import com.joelinjatovo.navette.models.RemoteLoaderResult;
import com.joelinjatovo.navette.utils.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderViewModel extends ViewModel implements Callback<RetrofitResponse<List<CarAndModel>>> {

    private static final String TAG = OrderViewModel.class.getSimpleName();

    private MutableLiveData<ClubAndPoint> club = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<List<CarAndModel>>> retrofitResult = new MutableLiveData<>();

    public MutableLiveData<ClubAndPoint> getClub() {
        return club;
    }

    public MutableLiveData<RemoteLoaderResult<List<CarAndModel>>> getRetrofitResult() {
        return retrofitResult;
    }


    public void setClub(ClubAndPoint item) {
        club.setValue(item);

        loadCars(item.getClub());
    }

    public void loadCars(Club club){
        Log.d(TAG, "service.getCars()");
        ClubApiService service = RetrofitClient.getInstance().create(ClubApiService.class);
        Call<RetrofitResponse<List<CarAndModel>>> call = service.getCars(club.getId());
        call.enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<RetrofitResponse<List<CarAndModel>>> call, Response<RetrofitResponse<List<CarAndModel>>> response) {
        Log.e(TAG, response.toString());
        if (response.body() != null) {
            Log.e(TAG, response.body().toString());
            retrofitResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
        }else{
            retrofitResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
        }
    }

    @Override
    public void onFailure(@NonNull Call<RetrofitResponse<List<CarAndModel>>> call, @NonNull Throwable t) {
        Log.e(TAG, t.getMessage(), t);
        retrofitResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
    }
}

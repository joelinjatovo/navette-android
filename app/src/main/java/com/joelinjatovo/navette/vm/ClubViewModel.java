package com.joelinjatovo.navette.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.api.clients.RetrofitClient;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.api.services.ClubApiService;
import com.joelinjatovo.navette.database.callback.UpsertCallback;
import com.joelinjatovo.navette.database.entity.ClubAndPoint;
import com.joelinjatovo.navette.database.repositories.ClubRepository;
import com.joelinjatovo.navette.utils.Log;
import com.joelinjatovo.navette.models.RemoteLoaderResult;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClubViewModel extends ViewModel implements Callback<RetrofitResponse<List<ClubAndPoint>>>, UpsertCallback<ClubAndPoint> {

    private static final String TAG = ClubViewModel.class.getSimpleName();

    private ClubRepository clubRepository;

    private MutableLiveData<RemoteLoaderResult<List<ClubAndPoint>>> retrofitResult = new MutableLiveData<>();

    private LiveData<List<ClubAndPoint>> clubs;

    ClubViewModel(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
        this.clubs = clubRepository.getList();
    }

    public MutableLiveData<RemoteLoaderResult<List<ClubAndPoint>>> getRetrofitResult() {
        return retrofitResult;
    }

    public LiveData<List<ClubAndPoint>> getClubs() {
        return clubs;
    }

    public LiveData<List<ClubAndPoint>> search(String search) {
        return clubRepository.search(search);
    }

    public void load(){
        Log.d(TAG, "service.getClubs()");
        ClubApiService service = RetrofitClient.getInstance().create(ClubApiService.class);
        Call<RetrofitResponse<List<ClubAndPoint>>> call = service.getClubs();
        call.enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<RetrofitResponse<List<ClubAndPoint>>> call, @NonNull Response<RetrofitResponse<List<ClubAndPoint>>> response) {
        Log.d(TAG, response.toString());
        if (response.body() != null) {
            Log.d(TAG, response.body().toString());
            ClubAndPoint[] items = new ClubAndPoint[response.body().getData().size()];
            for(int i = 0 ; i < response.body().getData().size(); i++){
                items[i] = response.body().getData().get(i);
            }
            clubRepository.upsert(this, items);
        }else{
            retrofitResult.setValue(new RemoteLoaderResult<List<ClubAndPoint>>(R.string.error_loading_clubs));
        }
    }

    @Override
    public void onFailure(@NonNull Call<RetrofitResponse<List<ClubAndPoint>>> call, @NonNull Throwable t) {
        Log.e(TAG, t.toString());
        retrofitResult.setValue(new RemoteLoaderResult<List<ClubAndPoint>>(R.string.error_loading_clubs));
    }

    @Override
    public void onUpsertError() {
        retrofitResult.setValue(new RemoteLoaderResult<List<ClubAndPoint>>(R.string.error_inserting_clubs));
    }

    @Override
    public void onUpsertSuccess(List<ClubAndPoint> items) {
        retrofitResult.setValue(new RemoteLoaderResult<List<ClubAndPoint>>(items));
    }
}

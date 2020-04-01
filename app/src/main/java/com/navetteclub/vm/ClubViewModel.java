package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.ClubApiService;
import com.navetteclub.database.callback.UpsertCallback;
import com.navetteclub.database.entity.ClubAndPoint;
import com.navetteclub.database.repositories.ClubRepository;
import com.navetteclub.utils.Log;
import com.navetteclub.models.RemoteLoaderResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClubViewModel extends ViewModel implements Callback<RetrofitResponse<List<ClubAndPoint>>>, UpsertCallback<ClubAndPoint> {

    private static final String TAG = ClubViewModel.class.getSimpleName();

    private ClubRepository clubRepository;

    private MutableLiveData<RemoteLoaderResult<List<ClubAndPoint>>> clubsResult = new MutableLiveData<>();

    private MutableLiveData<ClubAndPoint> club = new MutableLiveData<>();

    private LiveData<List<ClubAndPoint>> clubs;

    ClubViewModel(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
        this.clubs = clubRepository.getList();
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
            clubsResult.setValue(new RemoteLoaderResult<List<ClubAndPoint>>(R.string.error_loading_clubs));
        }
    }

    @Override
    public void onFailure(@NonNull Call<RetrofitResponse<List<ClubAndPoint>>> call, @NonNull Throwable t) {
        Log.e(TAG, t.toString());
        clubsResult.setValue(new RemoteLoaderResult<List<ClubAndPoint>>(R.string.error_loading_clubs));
    }

    @Override
    public void onUpsertError() {
        clubsResult.setValue(new RemoteLoaderResult<List<ClubAndPoint>>(R.string.error_inserting_clubs));
    }

    @Override
    public void onUpsertSuccess(List<ClubAndPoint> items) {
        clubsResult.setValue(new RemoteLoaderResult<List<ClubAndPoint>>(items));
    }

    public MutableLiveData<ClubAndPoint> getClub() {
        return club;
    }

    public void setClub(ClubAndPoint club) {
        this.club.setValue(club);
    }

    public MutableLiveData<RemoteLoaderResult<List<ClubAndPoint>>> getClubsResult() {
        return clubsResult;
    }
}

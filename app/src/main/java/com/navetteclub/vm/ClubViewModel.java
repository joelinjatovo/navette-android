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
import com.navetteclub.database.entity.Club;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.repositories.ClubRepository;
import com.navetteclub.utils.Log;
import com.navetteclub.models.RemoteLoaderResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClubViewModel extends ViewModel implements Callback<RetrofitResponse<List<Club>>> {

    private static final String TAG = ClubViewModel.class.getSimpleName();

    private ClubRepository clubRepository;

    private MutableLiveData<RemoteLoaderResult<List<Club>>> clubsResult = new MutableLiveData<>();

    private MutableLiveData<Club> club = new MutableLiveData<>();

    ClubViewModel(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public LiveData<List<Club>> search(String search) {
        return clubRepository.search(search);
    }

    public void load(){
        ClubApiService service = RetrofitClient.getInstance().create(ClubApiService.class);
        Call<RetrofitResponse<List<Club>>> call = service.index();
        call.enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<RetrofitResponse<List<Club>>> call, @NonNull Response<RetrofitResponse<List<Club>>> response) {
        Log.d(TAG, response.toString());
        if (response.body() != null) {
            Log.d(TAG, response.body().toString());
            List<Club> clubAndPoints = response.body().getData();
            if(clubAndPoints!=null){
                clubsResult.setValue(new RemoteLoaderResult<List<Club>>(clubAndPoints));
            }else{
                clubsResult.setValue(new RemoteLoaderResult<List<Club>>(R.string.error_loading_clubs));
            }
        }else{
            clubsResult.setValue(new RemoteLoaderResult<List<Club>>(R.string.error_loading_clubs));
        }
    }

    @Override
    public void onFailure(@NonNull Call<RetrofitResponse<List<Club>>> call, @NonNull Throwable t) {
        Log.e(TAG, t.toString());
        clubsResult.setValue(new RemoteLoaderResult<List<Club>>(R.string.error_loading_clubs));
    }

    public MutableLiveData<Club> getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club.setValue(club);
    }

    public MutableLiveData<RemoteLoaderResult<List<Club>>> getClubsResult() {
        return clubsResult;
    }
}

package com.joelinjatovo.navette.ui.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.api.clients.RetrofitClient;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.api.services.ClubApiService;
import com.joelinjatovo.navette.database.callback.FindCallback;
import com.joelinjatovo.navette.database.callback.UpsertCallback;
import com.joelinjatovo.navette.database.entity.Club;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.database.repository.ClubRepository;
import com.joelinjatovo.navette.database.repository.UserRepository;
import com.joelinjatovo.navette.ui.main.MainActivity;
import com.joelinjatovo.navette.utils.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClubViewModel extends ViewModel implements Callback<RetrofitResponse<List<Club>>> {

    private static final String TAG = ClubViewModel.class.getSimpleName();

    private ClubRepository clubRepository;

    private MutableLiveData<RemoteLoaderResult<List<Club>>> clubs = new MutableLiveData<>();

    ClubViewModel(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public MutableLiveData<RemoteLoaderResult<List<Club>>> getClubs() {
        return clubs;
    }

    public void load(){
        Log.d(TAG, "service.getClubs()");
        ClubApiService service = RetrofitClient.getInstance().create(ClubApiService.class);
        Call<RetrofitResponse<List<Club>>> call = service.getClubs();
        call.enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<RetrofitResponse<List<Club>>> call, @NonNull Response<RetrofitResponse<List<Club>>> response) {
        Log.d(TAG, response.toString());
        if (response.body() != null) {
            Log.d(TAG, response.body().toString());
            clubs.setValue(new RemoteLoaderResult<List<Club>>(response.body().getData()));
        }else{
            clubs.setValue(new RemoteLoaderResult<List<Club>>(R.string.error_loading_clubs));
        }
    }

    @Override
    public void onFailure(@NonNull Call<RetrofitResponse<List<Club>>> call, @NonNull Throwable t) {
        Log.e(TAG, t.toString());
        clubs.setValue(new RemoteLoaderResult<List<Club>>(R.string.error_loading_clubs));
    }
}

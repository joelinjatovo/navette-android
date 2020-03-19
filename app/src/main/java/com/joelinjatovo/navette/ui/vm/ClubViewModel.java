package com.joelinjatovo.navette.ui.vm;

import androidx.lifecycle.ViewModel;

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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ClubViewModel extends ViewModel implements FindCallback<Club> {

    private static final String TAG = ClubViewModel.class.getSimpleName();

    private ClubRepository clubRepository;

    ClubViewModel(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    public void load(Callback<RetrofitResponse<List<Club>>> callback){
        ClubApiService service = RetrofitClient.getInstance().create(ClubApiService.class);
        Call<RetrofitResponse<List<Club>>> call = service.getClubs();
        call.enqueue(callback);
    }

    @Override
    public void onFindError() {

    }

    @Override
    public void onFindSuccess(List<Club> items) {

    }

    public void upsert(UpsertCallback<Club> callback, List<Club> clubs) {
        //clubRepository.upsert(callback, clubs.toArray());
    }
}

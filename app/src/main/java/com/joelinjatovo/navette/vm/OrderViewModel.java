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
import com.joelinjatovo.navette.database.repositories.CarRepository;
import com.joelinjatovo.navette.models.RemoteLoaderResult;
import com.joelinjatovo.navette.utils.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderViewModel extends ViewModel implements Callback<RetrofitResponse<List<CarAndModel>>> {

    private static final String TAG = OrderViewModel.class.getSimpleName();

    private MutableLiveData<ClubAndPoint> club = new MutableLiveData<>();

    private MutableLiveData<List<CarAndModel>> cars = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<List<CarAndModel>>> retrofitResult = new MutableLiveData<>();

    private CarRepository carRepository;

    public OrderViewModel(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public MutableLiveData<ClubAndPoint> getClub() {
        return club;
    }

    public MutableLiveData<List<CarAndModel>> getCars() {
        return cars;
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
            CarAndModel[] items = new CarAndModel[response.body().getData().size()];
            for(int i = 0 ; i < response.body().getData().size(); i++){
                items[i] = response.body().getData().get(i);
            }
            carRepository.upsert(club, this, items);
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

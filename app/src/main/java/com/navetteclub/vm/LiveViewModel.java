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
import com.navetteclub.database.entity.ItemWithDatas;
import com.navetteclub.database.entity.RideWithDatas;
import com.navetteclub.database.entity.UserAndPoint;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveViewModel extends ViewModel {

    private static final String TAG = LiveViewModel.class.getSimpleName();

    private MutableLiveData<UserAndPoint> driverPositionLiveData = new MutableLiveData<>();

    public MutableLiveData<UserAndPoint> getDriverPositionLiveData() {
        return driverPositionLiveData;
    }

    public void setDriverPositionLiveData(UserAndPoint positionLiveData) {
        this.driverPositionLiveData.setValue(positionLiveData);
    }
}

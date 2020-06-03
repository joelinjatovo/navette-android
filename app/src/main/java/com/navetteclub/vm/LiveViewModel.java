package com.navetteclub.vm;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.database.entity.UserAndPoint;

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

package com.navetteclub.models;

import androidx.annotation.StringRes;

import com.google.gson.annotations.SerializedName;
import com.navetteclub.R;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.Ride;

public class RidePayload {
    @SerializedName("ride_id")
    private String rideId;

    @SerializedName("driver_id")
    private Long driverId;

    @SerializedName("oldStatus")
    private String oldStatus;

    @SerializedName("newStatus")
    private String newStatus;

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    @StringRes
    public int getNewStatusStringDesc() {
        if(newStatus==null){
            newStatus = oldStatus;
        }
        if(newStatus==null){
            return R.string.unknown_status;
        }
        switch (newStatus){
            case Ride.STATUS_PING: return R.string.desc_ride_status_ping;
            case Ride.STATUS_ACTIVE: return R.string.desc_ride_status_active;
            case Ride.STATUS_CANCELABLE: return R.string.desc_ride_status_cancelable;
            case Ride.STATUS_CANCELED: return R.string.desc_ride_status_canceled;
            case Ride.STATUS_COMPLETABLE: return R.string.desc_ride_status_completable;
            case Ride.STATUS_COMPLETED: return R.string.desc_ride_status_completed;
        }
        return R.string.unknown_status;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }
}

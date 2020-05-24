package com.navetteclub.api.models;

import com.google.gson.annotations.SerializedName;
import com.navetteclub.database.entity.Order;

public class RideParam {
    @SerializedName("id")
    public long rideId;

    public RideParam(long rideId) {
        this.rideId = rideId;
    }
}

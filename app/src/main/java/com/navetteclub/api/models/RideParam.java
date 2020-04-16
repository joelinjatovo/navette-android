package com.navetteclub.api.models;

import com.google.gson.annotations.SerializedName;
import com.navetteclub.database.entity.Order;

public class RideParam {
    @SerializedName("order_id")
    public long ride;

    public RideParam(long rideId) {
        this.ride = rideId;
    }
}

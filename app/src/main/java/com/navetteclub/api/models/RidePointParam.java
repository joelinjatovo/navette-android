package com.navetteclub.api.models;

import com.google.gson.annotations.SerializedName;

public class RidePointParam {
    @SerializedName("ride_point_id")
    public String ridePointId;

    public RidePointParam(String id) {
        this.ridePointId = id;
    }
}

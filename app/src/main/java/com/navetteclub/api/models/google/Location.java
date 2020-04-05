package com.navetteclub.api.models.google;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("lat")
    private Double lat;

    @SerializedName("lng")
    private Double lng;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @NonNull
    @Override
    public String toString() {
        return "[" + getLat()  + "," + getLng() + "]";
    }
}

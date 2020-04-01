package com.navetteclub.api.models;

import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("name")
    public String name;

    @SerializedName("lat")
    public Double lat;

    @SerializedName("lng")
    public Double lng;

    @SerializedName("alt")
    public Double alt;

    public Location(android.location.Location location){
        this.name = "Antananarivo";
        lat = location.getLatitude();
        lng = location.getLongitude();
        alt = 10.0; //location.getAltitude();
    }
}

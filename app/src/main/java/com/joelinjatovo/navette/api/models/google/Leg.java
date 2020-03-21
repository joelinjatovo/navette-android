package com.joelinjatovo.navette.api.models.google;

import com.google.gson.annotations.SerializedName;

public class Leg {

    @SerializedName("distance")
    private Distance distance;

    @SerializedName("duration")
    private Duration duration;

    @SerializedName("end_address")
    private String endAddress;

    @SerializedName("end_location")
    private Location endLocation;

    @SerializedName("start_address")
    private String startAddress;

    @SerializedName("start_location")
    private Location startLocation;

    @SerializedName("summary")
    private String summary;

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}

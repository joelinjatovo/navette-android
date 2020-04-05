package com.navetteclub.api.models.google;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class Leg {

    @SerializedName("steps")
    private Step[] steps;

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

    @NonNull
    @Override
    public String toString() {
        return "Leg[Steps= " + Arrays.toString(getSteps())
                + ", Distance=" + getDistance()
                + ", Duration=" + getDuration()
                + ", startLocation=" + getStartLocation()
                + ", endLocation=" + getEndLocation()  + "]";
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public Step[] getSteps() {
        return steps;
    }

    public void setSteps(Step[] steps) {
        this.steps = steps;
    }
}

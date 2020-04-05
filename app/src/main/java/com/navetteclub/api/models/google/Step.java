package com.navetteclub.api.models.google;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

class Step {
    @SerializedName("travel_mode")
    private String travelMode;

    @SerializedName("start_location")
    private Location startLocation;

    @SerializedName("end_location")
    private Location endLocation;

    @SerializedName("polyline")
    private OverviewPolyline polyline;

    @SerializedName("distance")
    private Distance distance;

    @SerializedName("duration")
    private Duration duration;

    @SerializedName("html_instructions")
    private String htmlInstructions;

    public String getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
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

    public OverviewPolyline getPolyline() {
        return polyline;
    }

    public void setPolyline(OverviewPolyline polyline) {
        this.polyline = polyline;
    }

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

    public String getHtmlInstructions() {
        return htmlInstructions;
    }

    public void setHtmlInstructions(String htmlInstructions) {
        this.htmlInstructions = htmlInstructions;
    }

    @NonNull
    @Override
    public String toString() {
        return "Leg[Distance=" + getDistance()
                + ", Duration=" + getDuration()
                + ", travelMode=" + getTravelMode()
                + ", startLocation=" + getStartLocation()
                + ", endLocation=" + getEndLocation()
                + ", htmlInstructions=" + getHtmlInstructions()  + "]";
    }
}

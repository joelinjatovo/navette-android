package com.joelinjatovo.navette.api.data.google;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OverviewPolyline {

    @SerializedName("points")
    private String points;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}

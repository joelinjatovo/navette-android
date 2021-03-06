package com.navetteclub.api.models.google;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

public class Route {

    @SerializedName("bounds")
    private Bound bounds;

    @SerializedName("copyrights")
    private String copyrights;

    @SerializedName("legs")
    private List<Leg> legs;

    @SerializedName("overview_polyline")
    private OverviewPolyline overviewPolyline;

    @SerializedName("waypoint_order")
    private Integer[] waypointOrder;

    public String getCopyrights() {
        return copyrights;
    }

    public void setCopyrights(String copyrights) {
        this.copyrights = copyrights;
    }

    public Bound getBounds() {
        return bounds;
    }

    public void setBounds(Bound bounds) {
        this.bounds = bounds;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }

    public OverviewPolyline getOverviewPolyline() {
        return overviewPolyline;
    }

    public void setOverviewPolyline(OverviewPolyline overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    public Integer[] getWaypointOrder() {
        return waypointOrder;
    }

    public void setWaypointOrder(Integer[] waypointOrder) {
        this.waypointOrder = waypointOrder;
    }

    @NonNull
    @Override
    public String toString() {
        return "Route[bounds=" + getBounds() + ", waypointOrder=" + Arrays.toString(getWaypointOrder()) + ", copyrights=" + copyrights + ", legs=" + getLegs() + ", overviewPolyline=" + getOverviewPolyline()  + "]";
    }
}

package com.joelinjatovo.navette.api.data.google;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GoogleDirectionResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("geocoded_waypoints")
    private GeocodedWaypoints[] geocodedWaypoints;

    @SerializedName("routes")
    private List<Route> routes;

    public GeocodedWaypoints[] getGeocodedWaypoints() {
        return geocodedWaypoints;
    }

    public void setGeocodedWaypoints(GeocodedWaypoints[] geocodedWaypoints) {
        this.geocodedWaypoints = geocodedWaypoints;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return "Direction[status=" + status + ", routes=" + routes.toString() + "]";
    }
}

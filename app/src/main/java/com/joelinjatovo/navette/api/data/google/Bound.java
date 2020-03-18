package com.joelinjatovo.navette.api.data.google;

import com.google.gson.annotations.SerializedName;

public class Bound {

    @SerializedName("northeast")
    private Location northeast;

    @SerializedName("southwest")
    private Location southwest;

    public Location getNortheast() {
        return northeast;
    }

    public void setNortheast(Location northeast) {
        this.northeast = northeast;
    }

    public Location getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Location southwest) {
        this.southwest = southwest;
    }
}

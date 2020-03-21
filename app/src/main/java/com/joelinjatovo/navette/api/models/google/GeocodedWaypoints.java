package com.joelinjatovo.navette.api.models.google;

import com.google.gson.annotations.SerializedName;

public class GeocodedWaypoints {

    @SerializedName("geocoder_status")
    private String geocoderStatus;

    @SerializedName("place_id")
    private String placeId;

    @SerializedName("types")
    private String[] types;

    public String getGeocoderStatus() {
        return geocoderStatus;
    }

    public void setGeocoderStatus(String geocoderStatus) {
        this.geocoderStatus = geocoderStatus;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }
}

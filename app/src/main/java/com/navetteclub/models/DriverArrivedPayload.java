package com.navetteclub.models;

import androidx.annotation.StringRes;

import com.google.gson.annotations.SerializedName;
import com.navetteclub.R;
import com.navetteclub.database.entity.Item;

import java.util.Date;

public class DriverArrivedPayload {
    @SerializedName("id")
    private String id;

    @SerializedName("item_id")
    private String itemId;

    @SerializedName("message")
    private String message;

    @SerializedName("duration")
    private String duration;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}

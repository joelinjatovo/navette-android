package com.navetteclub.models;

import androidx.annotation.StringRes;

import com.google.gson.annotations.SerializedName;
import com.navetteclub.R;
import com.navetteclub.database.entity.Item;
import com.navetteclub.database.entity.Ride;

import java.util.Date;

public class ItemPayload {
    @SerializedName("item_id")
    private String itemId;

    @SerializedName("ride_at")
    private Date rideAt;

    @SerializedName("oldStatus")
    private String oldStatus;

    @SerializedName("newStatus")
    private String newStatus;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    @StringRes
    public int getNewStatusStringDesc() {
        if(newStatus==null){
            newStatus = oldStatus;
        }
        if(newStatus==null){
            return R.string.unknown_status;
        }
        switch (newStatus){
            case Item.STATUS_PING: return R.string.desc_order_item_status_ping;
            case Item.STATUS_ACTIVE: return R.string.desc_order_item_status_active;
            case Item.STATUS_NEXT: return R.string.desc_order_item_status_next;
            case Item.STATUS_ARRIVED: return R.string.desc_order_item_status_arrived;
            case Item.STATUS_ONLINE: return R.string.desc_order_item_status_online;
            case Item.STATUS_CANCELED: return R.string.desc_order_item_status_canceled;
            case Item.STATUS_COMPLETED: return R.string.desc_order_item_status_completed;
        }
        return R.string.unknown_status;
    }

    public Date getRideAt() {
        return rideAt;
    }

    public void setRideAt(Date rideAt) {
        this.rideAt = rideAt;
    }
}

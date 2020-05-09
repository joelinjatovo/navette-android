package com.navetteclub.models;

import androidx.annotation.StringRes;

import com.google.gson.annotations.SerializedName;
import com.navetteclub.R;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.Ride;

public class OrderPayload {
    @SerializedName("order_id")
    private String orderId;

    @SerializedName("oldStatus")
    private String oldStatus;

    @SerializedName("newStatus")
    private String newStatus;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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
            case Order.STATUS_PING: return R.string.desc_order_status_ping;
            case Order.STATUS_OK: return R.string.desc_order_status_ok;
            case Order.STATUS_ACTIVE: return R.string.desc_order_status_active;
            case Order.STATUS_CANCELED: return R.string.desc_order_status_canceled;
            case Order.STATUS_COMPLETABLE: return R.string.desc_order_status_completable;
            case Order.STATUS_COMPLETED: return R.string.desc_order_status_completed;
        }
        return R.string.unknown_status;
    }
}

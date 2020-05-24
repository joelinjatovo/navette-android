package com.navetteclub.api.models;

import com.google.gson.annotations.SerializedName;
import com.navetteclub.database.entity.Order;

public class OrderParam {
    @SerializedName("id")
    public String orderId;

    public OrderParam(String orderRid) {
        this.orderId = orderRid;
    }

    public OrderParam(Order order) {
        this.orderId = order.getRid();
    }
}

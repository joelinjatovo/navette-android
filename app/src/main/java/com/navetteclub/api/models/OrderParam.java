package com.navetteclub.api.models;

import com.google.gson.annotations.SerializedName;
import com.navetteclub.database.entity.Order;

public class OrderParam {
    @SerializedName("order")
    public String order;

    public OrderParam(String orderRid) {
        this.order = orderRid;
    }

    public OrderParam(Order order) {
        this.order = order.getRid();
    }
}

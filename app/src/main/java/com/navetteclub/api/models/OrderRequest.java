package com.navetteclub.api.models;

import com.google.gson.annotations.SerializedName;
import com.navetteclub.database.entity.ItemWithDatas;
import com.navetteclub.database.entity.Order;

import java.util.List;

public class OrderRequest {
    @SerializedName("order")
    public Order order;

    @SerializedName("items")
    public List<ItemWithDatas> items;

    public OrderRequest setOrder(Order order) {
        this.order = order;
        return this;
    }

    public OrderRequest setItems(List<ItemWithDatas> items) {
        this.items = items;
        return this;
    }
}

package com.navetteclub.api.models;

import androidx.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;
import com.navetteclub.database.entity.ItemWithDatas;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.Item;
import com.navetteclub.database.entity.Point;

import java.util.ArrayList;
import java.util.List;

public class OrderRequest {
    @SerializedName("order")
    public Order order;

    @SerializedName("items")
    public List<ItemWithDatas> items;

    public OrderRequest(Order order, ItemWithDatas goItem, ItemWithDatas backItem){
        this.order = order;
        items= new ArrayList<>();
        if(goItem!=null){
            items.add(goItem);
        }
        if(backItem!=null){
            items.add(backItem);
        }
    }
}

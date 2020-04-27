package com.navetteclub.api.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.navetteclub.database.entity.Item;
import com.navetteclub.database.entity.ItemWithDatas;
import com.navetteclub.database.entity.Order;

import java.util.ArrayList;
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
        this.items = new ArrayList<>();
        if (items!=null) this.items.addAll(items);
        return this;
    }

    public OrderRequest checkElement() {
        if(items!=null && order!=null && order.getType()!=null){
            switch (order.getType()){
                case Order.TYPE_BACK:
                case Order.TYPE_GO:
                    if(this.items.size()>1) {
                        this.items.remove(1);
                    }
                break;
            }
        }
        return this;
    }

    @NonNull
    public String toString(){
        return "OrderRequest[ "
                + "\n order = " + order
                + "; \n items = "  + items
            + " ]";
    }
}

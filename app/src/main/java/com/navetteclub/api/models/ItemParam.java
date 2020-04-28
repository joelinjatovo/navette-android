package com.navetteclub.api.models;

import com.google.gson.annotations.SerializedName;

public class ItemParam {
    @SerializedName("item_id")
    public String itemId;

    public ItemParam(String id) {
        this.itemId = id;
    }
}

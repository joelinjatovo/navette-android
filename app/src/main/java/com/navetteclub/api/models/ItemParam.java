package com.navetteclub.api.models;

import com.google.gson.annotations.SerializedName;

public class ItemParam {
    @SerializedName("item_id")
    public long itemId;

    public ItemParam(long id) {
        this.itemId = id;
    }
}

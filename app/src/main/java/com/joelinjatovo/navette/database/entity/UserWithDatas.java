package com.joelinjatovo.navette.database.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.google.gson.annotations.SerializedName;

public class UserWithDatas {

    @SerializedName("user")
    @Embedded private User user;
}

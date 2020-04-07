package com.navetteclub.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Ride {

    @PrimaryKey
    private Long id;

    @SerializedName("status")
    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "driver_id")
    private Long driverId;

    @ColumnInfo(name = "car_id")
    private Long carId;

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    private Date createdAt;
}

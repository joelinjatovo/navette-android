package com.navetteclub.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

public class RidePointWithDatas {

    @SerializedName("ride_point")
    @Embedded
    private RidePoint ridePoint;

    @SerializedName("item")
    @Embedded
    private Item item;

    @SerializedName("point")
    @Embedded
    private Point point;

    @SerializedName("user")
    @Embedded
    private User user;

    public RidePoint getRidePoint() {
        return ridePoint;
    }

    public void setRidePoint(RidePoint ridePoint) {
        this.ridePoint = ridePoint;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}

package com.navetteclub.database.entity;

import androidx.room.Embedded;

import com.google.gson.annotations.SerializedName;

public class ItemWithDatas {

    @SerializedName("item")
    @Embedded
    private Item item;

    @SerializedName("point")
    @Embedded
    private Point point;

    @SerializedName("driver")
    @Embedded
    private User driver;

    @SerializedName("ride")
    @Embedded
    private Ride ride;

    public Item getItem() {
        return this.item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Point getPoint() {
        return this.point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }
}

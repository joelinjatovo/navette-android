package com.navetteclub.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Embedded;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    @SerializedName("club")
    @Embedded
    private ClubAndPoint clubAndPoint;

    @SerializedName("ridepoints")
    @Embedded
    private List<RidePoint> ridepoints;

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

    @NonNull
    public String toString() {
        return "\n ItemWithData[ "
                +  " \n \t item = " + item
                +  " \n \t point = " + point
            + " \n ]";
    }

    public List<RidePoint> getRidepoints() {
        return ridepoints;
    }

    public void setRidepoints(List<RidePoint> ridepoints) {
        this.ridepoints = ridepoints;
    }

    public ClubAndPoint getClubAndPoint() {
        return clubAndPoint;
    }

    public void setClubAndPoint(ClubAndPoint clubAndPoint) {
        this.clubAndPoint = clubAndPoint;
    }
}

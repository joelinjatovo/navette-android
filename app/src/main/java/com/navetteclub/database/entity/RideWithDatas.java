package com.navetteclub.database.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RideWithDatas {

    @SerializedName("ride")
    @Embedded
    private Ride ride;

    @SerializedName("driver")
    @Embedded
    private User driver;

    @SerializedName("car")
    @Embedded
    private Car car;

    @SerializedName("points")
    @Relation(
            parentColumn = "id",
            entityColumn = "point_id",
            associateBy = @Junction(RidePoint.class)
    )
    private List<Point> points;

    @SerializedName("orders")
    @Relation(
            parentColumn = "id",
            entityColumn = "order_id"
    )
    private List<Order> orders;

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
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

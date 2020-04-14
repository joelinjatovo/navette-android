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
    private List<RidePointWithDatas> points;

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

    public List<RidePointWithDatas> getPoints() {
        return points;
    }

    public void setPoints(List<RidePointWithDatas> points) {
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

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}

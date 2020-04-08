package com.navetteclub.database.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderWithDatas {

    @SerializedName("order")
    @Embedded
    private Order order;

    @SerializedName("user")
    @Embedded
    private User user;

    @SerializedName("club")
    @Embedded
    private Club club;

    @SerializedName("car")
    @Embedded
    private Car car;

    @SerializedName("origin")
    @Embedded
    private Point origin;

    @SerializedName("destination")
    @Embedded
    private Point destination;

    @SerializedName("retours")
    @Embedded
    private Point retours;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Point getOrigin() {
        return origin;
    }

    public void setOrigin(Point origin) {
        this.origin = origin;
    }

    public Point getDestination() {
        return destination;
    }

    public void setDestination(Point destination) {
        this.destination = destination;
    }

    public Point getRetours() {
        return retours;
    }

    public void setRetours(Point retours) {
        this.retours = retours;
    }
}

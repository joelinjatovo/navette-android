package com.joelinjatovo.navette.database.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderWithDatas {

    @SerializedName("order")
    @Embedded
    private Order order;

    @SerializedName("club")
    @Embedded
    private Club club;

    @SerializedName("car")
    @Embedded
    private Car car;

    @Relation(
            parentColumn = "id",
            entityColumn = "point_id",
            associateBy = @Junction(OrderPoint.class)
    )
    private List<Point> points;

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

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }
}
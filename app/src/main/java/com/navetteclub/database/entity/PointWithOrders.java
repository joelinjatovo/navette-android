package com.navetteclub.database.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class PointWithOrders {

    @Embedded
    private Point point;

    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(OrderPoint.class)
    )
    private List<Order> orders;

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}

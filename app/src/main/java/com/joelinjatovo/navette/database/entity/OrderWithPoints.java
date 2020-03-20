package com.joelinjatovo.navette.database.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class OrderWithPoints {

    @Embedded
    private Order order;

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

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }
}

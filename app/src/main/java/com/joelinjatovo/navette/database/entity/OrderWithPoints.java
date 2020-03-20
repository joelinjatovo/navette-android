package com.joelinjatovo.navette.database.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class OrderWithPoints {

    @Embedded
    private Order order;

    @Relation(
            parentColumn = "order_id",
            entity = Point.class,
            entityColumn = "point_id",
            associateBy = @Junction(
                    value = OrderPoint.class,
                    parentColumn = "order_id",
                    entityColumn = "point_id"
            )
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

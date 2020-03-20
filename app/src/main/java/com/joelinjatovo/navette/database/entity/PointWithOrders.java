package com.joelinjatovo.navette.database.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class PointWithOrders {

    @Embedded
    private Point point;

    @Relation(
            parentColumn = "id",
            entityColumn = "order_id",
            associateBy = @Junction(OrderPoint.class)
    )
    private List<Order> orders;
}

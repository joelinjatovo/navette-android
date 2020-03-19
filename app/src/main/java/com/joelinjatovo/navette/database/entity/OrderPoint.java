package com.joelinjatovo.navette.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(
    tableName = "order_point"
)
public class OrderPoint {
    @PrimaryKey
    private Long id;

    @SerializedName("id")
    @ColumnInfo(name = "rid")
    private String rid; // Remote id

    @SerializedName("type")
    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "order_id")
    private Long orderId;

    @ColumnInfo(name = "point_id")
    private Long pointId;
}

package com.navetteclub.database.entity;

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
    private String pointId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }
}

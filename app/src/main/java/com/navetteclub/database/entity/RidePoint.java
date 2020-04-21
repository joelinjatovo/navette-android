package com.navetteclub.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(
    tableName = "ride_point"
)
public class RidePoint {

    public static final String STATUS_PING = "ping";

    public static final String STATUS_NEXT = "next";

    public static final String STATUS_ACTIVE = "active";

    public static final String STATUS_ONLINE = "online";

    public static final String STATUS_CANCELED = "canceled";

    public static final String STATUS_COMPLETED = "completed";

    @PrimaryKey
    private Long id;

    @SerializedName("rid")
    @ColumnInfo(name = "rid")
    private String rid;

    @SerializedName("status")
    @ColumnInfo(name = "status")
    private String status;

    @SerializedName("type")
    @ColumnInfo(name = "type")
    private String type;

    @SerializedName("order")
    @ColumnInfo(name = "order")
    private int order;

    @SerializedName("distance_value")
    @ColumnInfo(name = "distance_value")
    private Long distanceValue;

    @SerializedName("distance")
    @ColumnInfo(name = "distance")
    private String distance;

    @SerializedName("duration_value")
    @ColumnInfo(name = "duration_value")
    private Long durationValue;

    @SerializedName("duration")
    @ColumnInfo(name = "duration")
    private String duration;

    @SerializedName("direction")
    @ColumnInfo(name = "direction")
    private String direction;

    @SerializedName("ride_id")
    @ColumnInfo(name = "ride_id")
    private Long rideId;

    @SerializedName("point_id")
    @ColumnInfo(name = "point_id")
    private String pointId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public Long getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(Long distanceValue) {
        this.distanceValue = distanceValue;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Long getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(Long durationValue) {
        this.durationValue = durationValue;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}

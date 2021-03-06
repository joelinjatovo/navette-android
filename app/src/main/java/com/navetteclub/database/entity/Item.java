package com.navetteclub.database.entity;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.navetteclub.R;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;

@Entity(
    tableName = "items"
)
public class Item {

    public static final String STATUS_PING = "ping";
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_CANCELED = "canceled";
    public static final String STATUS_NEXT = "next";
    public static final String STATUS_ARRIVED = "arrived";
    public static final String STATUS_ONLINE = "online";
    public static final String STATUS_COMPLETED = "completed";
    public static final String TYPE_GO = "go";
    public static final String TYPE_BACK = "back";
    @PrimaryKey
    private Long id;

    @SerializedName("rid")
    @ColumnInfo(name = "rid")
    private String rid; // Remote id

    @SerializedName("type")
    @ColumnInfo(name = "type")
    private String type;

    @SerializedName("status")
    @ColumnInfo(name = "status")
    private String status;

    @SerializedName("ride_status")
    @ColumnInfo(name = "ride_status")
    private String rideStatus;

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

    @SerializedName("ride_at")
    @ColumnInfo(name = "ride_at")
    private Date rideAt;

    @SerializedName("completed_at")
    @ColumnInfo(name = "completed_at")
    private Date completedAt;

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "point_id")
    private String pointId;

    @ColumnInfo(name = "ride_id")
    private String rideId;

    @ColumnInfo(name = "driver_id")
    private String driverId;

    @ColumnInfo(name = "order_id")
    private String orderId;

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }

    public Long getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(Long distanceValue) {
        this.distanceValue = distanceValue;
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

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getRideAt() {
        return rideAt;
    }

    public void setRideAt(Date rideAt) {
        this.rideAt = rideAt;
    }

    @NonNull
    public String toString(){
        return "User[id=" + id
                + "; status=" + status
                + "; rideStatus=" + rideStatus
                + "; direction=" + direction
                + "; distance=" + distance
                + "; distanceValue=" + distanceValue
                + "; duration=" + duration
                + "; durationValue=" + durationValue
                + "; rideAt=" + rideAt
        + "]";
    }

    @StringRes
    public int getStatusRes() {
        if(status==null){
            return R.string.status_ping;
        }
        switch (status){
            case STATUS_ACTIVE: return R.string.status_active;
            case STATUS_ARRIVED: return R.string.status_arrived;
            case STATUS_NEXT: return R.string.status_next;
            case STATUS_ONLINE: return R.string.status_online;
            case STATUS_CANCELED: return R.string.status_canceled;
            case STATUS_COMPLETED: return R.string.status_completed;
            case STATUS_PING: default: return R.string.status_ping;
        }
    }
}

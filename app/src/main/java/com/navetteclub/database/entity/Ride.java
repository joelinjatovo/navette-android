package com.navetteclub.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;


@Entity(
        tableName = "rides"
)
public class Ride {

    public static final String STATUS_PING = "ping";
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_COMPLETABLE = "completable";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_CANCELED = "canceled";
    @PrimaryKey
    private Long id;

    @SerializedName("status")
    @ColumnInfo(name = "status")
    private String status;

    @SerializedName("distance")
    @ColumnInfo(name = "distance")
    private Long distance;

    @SerializedName("delay")
    @ColumnInfo(name = "delay")
    private Long delay;

    @SerializedName("direction")
    @ColumnInfo(name = "direction")
    private String direction;

    @ColumnInfo(name = "driver_id")
    private Long driverId;

    @ColumnInfo(name = "car_id")
    private Long carId;

    @SerializedName("started_at")
    @ColumnInfo(name = "started_at")
    private Date startedAt;

    @SerializedName("completed_at")
    @ColumnInfo(name = "completed_at")
    private Date completedAt;

    @SerializedName("canceled_at")
    @ColumnInfo(name = "canceled_at")
    private Date canceledAt;

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Date completedAt) {
        this.completedAt = completedAt;
    }

    public Date getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(Date canceledAt) {
        this.canceledAt = canceledAt;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}

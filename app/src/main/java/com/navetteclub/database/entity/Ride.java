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

    @PrimaryKey
    private Long id;

    @SerializedName("status")
    @ColumnInfo(name = "status")
    private String status;

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
}

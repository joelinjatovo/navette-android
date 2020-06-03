package com.navetteclub.database.entity;

import android.text.format.DateUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.navetteclub.api.models.google.Route;

import java.util.Date;
import java.util.List;

@Entity(
    tableName = "rides"
)
public class Ride {

    public static final String STATUS_PING = "ping";

    public static final String STATUS_ACTIVE = "active";

    public static final String STATUS_COMPLETABLE = "completable";

    public static final String STATUS_COMPLETED = "completed";

    public static final String STATUS_CANCELABLE = "cancelable";

    public static final String STATUS_CANCELED = "canceled";

    @PrimaryKey
    private Long id;

    @SerializedName("status")
    @ColumnInfo(name = "status")
    private String status;

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

    @ColumnInfo(name = "driver_id")
    private Long driverId;

    @ColumnInfo(name = "car_id")
    private Long carId;

    @SerializedName("start_at")
    @ColumnInfo(name = "start_at")
    private Date startAt;

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

    @SerializedName("route")
    @Ignore
    private Route route;

    @SerializedName("club")
    @Ignore
    private Club club;

    @SerializedName("driver")
    @Ignore
    private User driver;

    @SerializedName("car")
    @Ignore
    private Car car;

    @SerializedName("ridepoints")
    @Ignore
    private List<RidePoint> ridepoints;

    @SerializedName("items")
    @Ignore
    private List<Item> items;

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

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public List<RidePoint> getRidepoints() {
        return ridepoints;
    }

    public void setRidepoints(List<RidePoint> ridepoints) {
        this.ridepoints = ridepoints;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}

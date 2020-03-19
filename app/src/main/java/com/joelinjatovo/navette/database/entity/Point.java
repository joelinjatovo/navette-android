package com.joelinjatovo.navette.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(
    tableName = "users",
    indices = {
        @Index(value = {"facebook_id"}),
    }
)
public class Point {
    @PrimaryKey
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    @ColumnInfo(name = "name")
    private String name;

    @SerializedName("type")
    @ColumnInfo(name = "type")
    private String type;

    @SerializedName("lat")
    @ColumnInfo(name = "lat")
    private Double lat;

    @SerializedName("lng")
    @ColumnInfo(name = "lng")
    private Double lng;

    @SerializedName("alt")
    @ColumnInfo(name = "alt")
    private Double alt;

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getAlt() {
        return alt;
    }

    public void setAlt(Double alt) {
        this.alt = alt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

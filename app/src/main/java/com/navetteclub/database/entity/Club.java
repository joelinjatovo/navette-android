package com.navetteclub.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(
    tableName = "clubs",
    indices = {
            @Index(value = {"point_id"})
    },
    foreignKeys = @ForeignKey(
            entity = Point.class,
            parentColumns = "id",
            childColumns = "point_id",
            onDelete = ForeignKey.NO_ACTION
    )
)
public class Club {
    @PrimaryKey
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    @ColumnInfo(name = "name")
    private String name;

    @SerializedName("image_url")
    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @ColumnInfo(name = "point_id")
    private String pointId;

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    public Club() {
    }

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NonNull
    public String toString(){
        return "Club[id=" + id
                + "; name=" + name
                + "; imageUrl=" + imageUrl
            + "]";
    }
}

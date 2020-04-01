package com.navetteclub.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(
    tableName = "user_point"
)
public class UserPoint {
    @PrimaryKey
    private Long id;

    @SerializedName("id")
    @ColumnInfo(name = "rid")
    private String rid; // Remote id

    @ColumnInfo(name = "user_id")
    private Long userId;

    @ColumnInfo(name = "point_id")
    private Long pointId;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPointId() {
        return pointId;
    }

    public void setPointId(Long pointId) {
        this.pointId = pointId;
    }
}

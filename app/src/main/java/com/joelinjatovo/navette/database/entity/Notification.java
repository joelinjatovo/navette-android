package com.joelinjatovo.navette.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.UUID;

@Entity(
        tableName = "notifications"
)
public class Notification {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    private String id = UUID.randomUUID().toString();

    @SerializedName("type")
    @ColumnInfo(name = "type")
    private String type;

    @SerializedName("notifiable_type")
    @ColumnInfo(name = "notifiable_type")
    private String notifiableType;

    @SerializedName("notifiable_id")
    @ColumnInfo(name = "notifiable_id")
    private Long notifiableId;

    @SerializedName("data")
    @ColumnInfo(name = "data")
    private Object data;

    @SerializedName("read_at")
    @ColumnInfo(name = "read_at")
    private Date readAt;

    @SerializedName("created_at")
    @ColumnInfo(name = "created_at")
    private Date createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotifiableType() {
        return notifiableType;
    }

    public void setNotifiableType(String notifiableType) {
        this.notifiableType = notifiableType;
    }

    public Long getNotifiableId() {
        return notifiableId;
    }

    public void setNotifiableId(Long notifiableId) {
        this.notifiableId = notifiableId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Date getReadAt() {
        return readAt;
    }

    public void setReadAt(Date readAt) {
        this.readAt = readAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}

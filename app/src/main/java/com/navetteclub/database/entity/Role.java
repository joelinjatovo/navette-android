package com.navetteclub.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(
    tableName = "roles"
)
public class Role {
    @PrimaryKey
    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    @ColumnInfo(name = "name")
    private String name;

    @SerializedName("description")
    @ColumnInfo(name = "description")
    private String description;

    @SerializedName("priority")
    @ColumnInfo(name = "priority")
    private Integer priority;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}

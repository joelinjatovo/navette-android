package com.navetteclub.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Relation;

import com.google.gson.annotations.SerializedName;

public class UserAndPoint {

    @SerializedName("point")
    @Embedded
    private Point point;

    @SerializedName("user")
    @Embedded
    private User user;

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @NonNull
    public String toString(){
        return "UserAndPoint[user=" + user + "; point=" + point + "]";
    }
}

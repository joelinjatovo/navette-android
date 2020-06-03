package com.navetteclub.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Ignore;

import com.google.gson.annotations.SerializedName;

public class UserAndPoint {

    @SerializedName("point")
    @Ignore
    private Point point;

    @SerializedName("user")
    @Ignore
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

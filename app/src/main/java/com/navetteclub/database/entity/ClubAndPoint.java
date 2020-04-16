package com.navetteclub.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Relation;

import com.google.gson.annotations.SerializedName;

public class ClubAndPoint {

    @SerializedName("point")
    @Embedded
    private Point point;

    @SerializedName("club")
    @Relation(
            parentColumn = "id",
            entityColumn =  "point_id"
    )
    private Club club;

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    @NonNull
    public String toString(){
        return "ClubAndPoint[id=" + club + ";" + point + "]";
    }
}

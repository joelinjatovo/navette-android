package com.navetteclub.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Ignore;

import com.google.gson.annotations.SerializedName;

public class ClubAndPoint {

    @SerializedName("point")
    @Ignore
    private Point point;

    @SerializedName("club")
    @Ignore
    private Club club;

    public ClubAndPoint(Club club, Point point) {
        this.club = club;
        this.point = point;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @NonNull
    public String toString(){
        return "ClubAndPoint[club=" + club + "; point=" + point + "]";
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }
}

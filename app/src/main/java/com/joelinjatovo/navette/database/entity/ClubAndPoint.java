package com.joelinjatovo.navette.database.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ClubAndPoint {

    @Embedded
    private Point point;

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
}

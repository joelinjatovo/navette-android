package com.joelinjatovo.navette.database.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class UserWithPoints {

    @Embedded
    private User user;

    @Relation(
            parentColumn = "point_id",
            entity = User.class,
            entityColumn = "user_id",
            associateBy = @Junction(
                    value = UserPoint.class,
                    parentColumn = "point_id",
                    entityColumn = "user_id"
            )
    )
    private List<Point> points;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }
}

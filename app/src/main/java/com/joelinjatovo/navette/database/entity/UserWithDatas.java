package com.joelinjatovo.navette.database.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserWithDatas {

    @SerializedName("user")
    @Embedded private User user;

    @SerializedName("roles")
    @Relation(
            parentColumn = "role_id",
            entity = Role.class,
            entityColumn = "user_id",
            associateBy = @Junction(
                    value = UserPoint.class,
                    parentColumn = "user_id",
                    entityColumn = "role_id"
            )
    )
    private List<Role> roles;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}

package com.joelinjatovo.navette.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(
    tableName = "users",
    indices = {
        @Index(value = {"token"}),
        @Index(value = {"refresh_token"}),
        @Index(value = {"phone"}, unique = true)
    }
)
public class User {
    @PrimaryKey
    @SerializedName("id")
    public int id;

    @SerializedName("phone")
    @ColumnInfo(name = "phone")
    public String phone;

    @SerializedName("name")
    @ColumnInfo(name = "name")
    public String name;

    @SerializedName("locale")
    @ColumnInfo(name = "locale")
    public String locale;

    @ColumnInfo(name = "token")
    public String token;

    @ColumnInfo(name = "refresh_token")
    public String refresh_token;

    public String toString(){
        return "User[id=" + id + "; phone=" + phone + "; name=" + name + "; locale=" + locale + "]";
    }
}

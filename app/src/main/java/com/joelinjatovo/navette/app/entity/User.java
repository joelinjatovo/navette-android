package com.joelinjatovo.navette.app.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

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

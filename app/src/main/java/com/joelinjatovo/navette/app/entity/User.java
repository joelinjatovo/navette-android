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
    private int id;

    @SerializedName("phone")
    @ColumnInfo(name = "phone")
    private String phone;

    @SerializedName("name")
    @ColumnInfo(name = "name")
    private String name;

    @SerializedName("locale")
    @ColumnInfo(name = "locale")
    private String locale;

    @ColumnInfo(name = "token")
    private String token;

    @ColumnInfo(name = "refresh_token")
    private String refresh_token;

    public String toString(){
        return "User[id=" + id + "; phone=" + phone + "; name=" + name + "; locale=" + locale + "]";
    }
}

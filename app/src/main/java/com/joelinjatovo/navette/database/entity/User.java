package com.joelinjatovo.navette.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(
    tableName = "users",
    indices = {
        @Index(value = {"facebook_id"}),
        @Index(value = {"token"}),
        @Index(value = {"refresh_token"}),
        @Index(value = {"email"}, unique = true),
        @Index(value = {"phone"}, unique = true)
    }
)
public class User {
    @PrimaryKey
    @SerializedName("id")
    private int id;

    @SerializedName("facebook_id")
    @ColumnInfo(name = "facebook_id")
    private String facebookId;

    @SerializedName("name")
    @ColumnInfo(name = "name")
    private String name;

    @SerializedName("email")
    @ColumnInfo(name = "email")
    private String email;

    @SerializedName("phone")
    @ColumnInfo(name = "phone")
    private String phone;

    @SerializedName("locale")
    @ColumnInfo(name = "locale")
    private String locale;

    @SerializedName("verified")
    @ColumnInfo(name = "verified")
    private Boolean verified;

    @ColumnInfo(name = "token")
    private String token;

    @ColumnInfo(name = "token_expires")
    private Date tokenExpires;

    @ColumnInfo(name = "refresh_token")
    private String refreshToken;

    @ColumnInfo(name = "refresh_token_expires")
    private Date refreshTokenExpires;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getTokenExpires() {
        return tokenExpires;
    }

    public void setTokenExpires(Date tokenExpires) {
        this.tokenExpires = tokenExpires;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getRefreshTokenExpires() {
        return refreshTokenExpires;
    }

    public void setRefreshTokenExpires(Date refreshTokenExpires) {
        this.refreshTokenExpires = refreshTokenExpires;
    }

    @NonNull
    public String toString(){
        return "User[id=" + id + "; phone=" + phone + "; name=" + name + "; locale=" + locale + "]";
    }
}

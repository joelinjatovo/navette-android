package com.navetteclub.database.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

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
    private Long id;

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

    @SerializedName("token")
    @ColumnInfo(name = "token")
    private String token;

    @SerializedName("token_expires")
    @ColumnInfo(name = "token_expires")
    private Long tokenExpires;

    @SerializedName("refresh_token")
    @ColumnInfo(name = "refresh_token")
    private String refreshToken;

    @SerializedName("refresh_token_expires")
    @ColumnInfo(name = "refresh_token_expires")
    private Long refreshTokenExpires;

    @SerializedName("image_url")
    @ColumnInfo(name = "image_url")
    private String imageUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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


    public String getAuthorizationToken() {
        return "Bearer " + token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getTokenExpires() {
        return tokenExpires;
    }

    public void setTokenExpires(Long tokenExpires) {
        this.tokenExpires = tokenExpires;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getRefreshTokenExpires() {
        return refreshTokenExpires;
    }

    public void setRefreshTokenExpires(Long refreshTokenExpires) {
        this.refreshTokenExpires = refreshTokenExpires;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NonNull
    public String toString(){
        return "User[id=" + id + "; phone=" + phone + "; name=" + name + "; locale=" + locale + "; token=" + token  + "; tokenExpires=" + tokenExpires + "]";
    }
}

package com.joelinjatovo.navette.database.entity;

import com.google.gson.annotations.SerializedName;

public class AccessToken {
    @SerializedName("token")
    public String token;

    @SerializedName("expires")
    public Integer expires;

    @SerializedName("refresh_token")
    public String refreshToken;

    @SerializedName("refresh_token_expires")
    public Integer refreshTokenExpires;
}

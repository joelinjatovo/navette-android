package com.navetteclub.api.models;

import com.google.gson.annotations.SerializedName;

public class Register {
    @SerializedName("facebook_id")
    public String facebookId;

    @SerializedName("name")
    public String name;

    @SerializedName("phone")
    public String phone;

    @SerializedName("email")
    public String email;

    @SerializedName("picture_url")
    public String pictureUrl;

    @SerializedName("password")
    public String password;

    public Register(){
    }

    public Register(String name, String phone, String password){
        this.name = name;
        this.phone = phone;
        this.password = password;
    }

    public Register(String name, String phone, String password, String email, String pictureUrl){
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.email = email;
        this.pictureUrl = pictureUrl;
    }
}

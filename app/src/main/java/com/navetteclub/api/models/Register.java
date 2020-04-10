package com.navetteclub.api.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.navetteclub.database.entity.User;

import java.util.Arrays;

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

    public Register(User user) {
        this.name = user.getName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
    }

    @NonNull
    @Override
    public String toString() {
        return "RetrofitResponse["
                + "; facebookId=" + facebookId
                + "; name=" + name
                + "; email=" + email
                + "; phone=" + phone
                + "; pictureUrl=" + pictureUrl
                + "; password=" + password
                + "]";
    }
}

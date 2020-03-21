package com.joelinjatovo.navette.api.models;

import com.google.gson.annotations.SerializedName;

public class Login {
    @SerializedName("phone")
    public String phone;

    @SerializedName("password")
    public String password;

    public Login(String phone, String password){
        this.phone = phone;
        this.password = password;
    }
}

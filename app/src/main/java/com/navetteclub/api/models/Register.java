package com.navetteclub.api.models;

import com.google.gson.annotations.SerializedName;

public class Register {
    @SerializedName("name")
    public String name;

    @SerializedName("phone")
    public String phone;

    @SerializedName("password")
    public String password;

    public Register(String name, String phone, String password){
        this.name = name;
        this.phone = phone;
        this.password = password;
    }
}

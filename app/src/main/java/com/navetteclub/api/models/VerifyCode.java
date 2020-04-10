package com.navetteclub.api.models;

import com.google.gson.annotations.SerializedName;

public class VerifyCode {
    @SerializedName("code")
    public String code;

    public VerifyCode(String code) {
        this.code = code;
    }
}

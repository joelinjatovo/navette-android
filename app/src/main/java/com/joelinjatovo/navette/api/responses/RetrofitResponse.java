package com.joelinjatovo.navette.api.responses;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class RetrofitResponse<T> {

    @SerializedName("code")
    private int code;

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("errors")
    private String[] errors;

    @SerializedName("data")
    private T data;

    public RetrofitResponse(T data) {
        this.data = data;
    }

    public T getData() {
        return this.data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "RetrofitResponse[errors=" + Arrays.toString(this.getErrors()) + "; status=" + getStatus() + "; message=" + getMessage() + "]";
    }
}

package com.joelinjatovo.navette.api.responses;

import com.google.gson.annotations.SerializedName;
import com.joelinjatovo.navette.api.models.Pagination;

import java.util.Arrays;

public class RetrofitResponse<T> {

    @SerializedName("status")
    private int status;

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("validation")
    private Object validation;

    @SerializedName("errors")
    private Object[] errors;

    @SerializedName("data")
    private T data;

    @SerializedName("pagination")
    private Pagination pagination;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object[] getErrors() {
        return errors;
    }

    public void setErrors(Object[] errors) {
        this.errors = errors;
    }

    public Object getValidation() {
        return validation;
    }

    public void setValidation(Object validation) {
        this.validation = validation;
    }

    public boolean isSuccess(){
        return this.code == 0;
    }

    @Override
    public String toString() {
        return "RetrofitResponse["
                + "errors=" + Arrays.toString(this.getErrors())
                + "; status=" + getStatus()
                + "; code=" + getCode()
                + "; message=" + getMessage()
                + "; data=" + getData()
                + "; validation=" + getValidation()
            + "]";
    }
}

package com.navetteclub.api.responses;

import androidx.annotation.StringRes;

import com.google.gson.annotations.SerializedName;
import com.navetteclub.R;
import com.navetteclub.api.models.Pagination;
import com.navetteclub.models.RemoteLoaderResult;

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
                + "; pagination=" + getPagination()
            + "]";
    }

    @StringRes
    public Integer getErrorResString() {
        switch (getCode()){
            case 106: return R.string.error_ride_no_route;
            case 114: return R.string.error_ride_empty_points;
            case 115: return R.string.error_ride_no_route;
        }

        switch (getStatus()){
            case 400: return R.string.error_400;
            case 401: return R.string.error_401;
            case 402: return R.string.error_402;
            case 403: return R.string.error_403;
            case 404: return R.string.error_404;
            case 422: return R.string.error_422;
            default: return R.string.error_unkown;
        }
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}

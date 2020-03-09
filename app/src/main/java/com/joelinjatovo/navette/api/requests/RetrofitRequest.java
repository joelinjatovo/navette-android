package com.joelinjatovo.navette.api.requests;

import com.google.gson.annotations.SerializedName;

public class RetrofitRequest<T> {

    @SerializedName("payload")
    private T payload;

    public RetrofitRequest(T payload) {
        this.payload = payload;
    }

    public T getPayload() {
        return this.payload;
    }

    @Override
    public String toString() {
        return "ApiRequest[payload=" + this.getPayload().toString() + "]";
    }
}

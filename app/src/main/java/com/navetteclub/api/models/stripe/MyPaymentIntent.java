package com.navetteclub.api.models.stripe;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class MyPaymentIntent {
    @SerializedName("client_secret")
    public String clientSecret;

    @SerializedName("publishable_key")
    public String publishableKey;

    @NonNull
    @Override
    public String toString() {
        return "MyPaymentIntent["
                + "; clientSecret=" + clientSecret
                + "; publishableKey=" + publishableKey
                + "]";
    }
}

package com.navetteclub.api.models.google;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Duration {

    @SerializedName("value")
    private Long value;

    @SerializedName("text")
    private String text;

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @NonNull
    @Override
    public String toString() {
        return "Duration[value=" + getValue() + ", text=" + getText()  + "]";
    }
}

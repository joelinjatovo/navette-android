package com.navetteclub.models;

import androidx.annotation.Nullable;

public class PhoneFormState {

    @Nullable
    private Integer phoneError;

    private boolean isDataValid;

    public PhoneFormState(@Nullable Integer phoneError) {
        this.phoneError = phoneError;
        this.isDataValid = false;
    }

    public PhoneFormState(boolean isDataValid) {
        this.phoneError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getPhoneError() {
        return phoneError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}

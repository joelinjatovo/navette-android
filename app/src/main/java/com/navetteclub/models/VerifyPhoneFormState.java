package com.navetteclub.models;

import androidx.annotation.Nullable;

public class VerifyPhoneFormState {

    @Nullable
    private Integer verifyCodeError;

    private boolean isDataValid;

    public VerifyPhoneFormState(@Nullable Integer verifyCodeError) {
        this.verifyCodeError = verifyCodeError;
        this.isDataValid = false;
    }

    public VerifyPhoneFormState(boolean isDataValid) {
        this.verifyCodeError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getVerifyCodeError() {
        return verifyCodeError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}

package com.joelinjatovo.navette.models;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
public class LoginFormState {

    @Nullable
    private Integer phoneError;

    @Nullable
    private Integer passwordError;

    private boolean isDataValid;

    public LoginFormState(@Nullable Integer phoneError, @Nullable Integer passwordError) {
        this.phoneError = phoneError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    public LoginFormState(boolean isDataValid) {
        this.phoneError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getPhoneError() {
        return phoneError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}

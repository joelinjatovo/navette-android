package com.joelinjatovo.navette.ui.auth.register;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.joelinjatovo.navette.database.entity.User;

/**
 * Registration result : success (user details) or error message.
 */
public class RegisterResult {
    @Nullable
    private User success;

    @Nullable
    @StringRes
    private Integer error;

    public RegisterResult(@Nullable @StringRes Integer error) {
        this.error = error;
    }

    public RegisterResult(@Nullable User success) {
        this.success = success;
    }

    @Nullable
    public User getSuccess() {
        return success;
    }

    @Nullable
    @StringRes
    public Integer getError() {
        return error;
    }
}

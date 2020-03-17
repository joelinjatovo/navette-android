package com.joelinjatovo.navette.ui.main.auth.login;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.joelinjatovo.navette.database.entity.User;

/**
 * Authentication result : success (user details) or error message.
 */
public class LoginResult {
    @Nullable
    private User success;

    @Nullable
    private Integer error;

    public LoginResult(@Nullable @StringRes Integer error) {
        this.error = error;
    }

    public LoginResult(@Nullable User success) {
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

package com.joelinjatovo.navette.ui.auth.login;

import androidx.annotation.Nullable;

import com.joelinjatovo.navette.database.entity.User;

/**
 * Authentication result : success (user details) or error message.
 */
public class LoginResult {
    @Nullable
    private User success;

    @Nullable
    private int error;

    public LoginResult(@Nullable Integer error) {
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
    public Integer getError() {
        return error;
    }
}

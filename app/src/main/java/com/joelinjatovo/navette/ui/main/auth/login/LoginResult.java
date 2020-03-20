package com.joelinjatovo.navette.ui.main.auth.login;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.database.entity.UserWithRoles;

/**
 * Authentication result : success (user details) or error message.
 */
public class LoginResult {
    @Nullable
    private UserWithRoles success;

    @Nullable
    private Integer error;

    public LoginResult(@Nullable @StringRes Integer error) {
        this.error = error;
    }

    public LoginResult(@Nullable UserWithRoles success) {
        this.success = success;
    }

    @Nullable
    public UserWithRoles getSuccess() {
        return success;
    }

    @Nullable
    @StringRes
    public Integer getError() {
        return error;
    }
}

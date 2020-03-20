package com.joelinjatovo.navette.ui.main.auth.register;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.database.entity.UserWithRoles;

/**
 * Registration result : success (user details) or error message.
 */
public class RegisterResult {
    @Nullable
    private UserWithRoles success;

    @Nullable
    @StringRes
    private Integer error;

    public RegisterResult(@Nullable @StringRes Integer error) {
        this.error = error;
    }

    public RegisterResult(@Nullable UserWithRoles success) {
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

package com.joelinjatovo.navette.models;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

public class RemoteLoaderResult<T> {
    @Nullable
    private T success;

    @Nullable
    private Integer error;

    public RemoteLoaderResult(@Nullable @StringRes Integer error) {
        this.error = error;
    }

    public RemoteLoaderResult(@Nullable T success) {
        this.success = success;
    }

    @Nullable
    public T getSuccess() {
        return success;
    }

    @Nullable
    @StringRes
    public Integer getError() {
        return error;
    }
}

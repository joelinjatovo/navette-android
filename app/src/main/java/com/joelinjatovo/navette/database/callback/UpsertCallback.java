package com.joelinjatovo.navette.database.callback;

import com.joelinjatovo.navette.database.entity.User;

import java.util.List;

public interface UpsertCallback<T> {
    void onUpsertError();
    void onUpsertSuccess(List<T> items);
}

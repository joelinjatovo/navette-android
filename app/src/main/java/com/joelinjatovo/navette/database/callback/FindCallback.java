package com.joelinjatovo.navette.database.callback;

import java.util.List;

public interface FindCallback<T> {
    void onFindError();
    void onFindSuccess(List<T> items);
}

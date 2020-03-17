package com.joelinjatovo.navette.database.callback;

import java.util.List;

public interface InsertCallback<T> {
    void onInsertError();
    void onInsertSuccess(List<T> items);
}

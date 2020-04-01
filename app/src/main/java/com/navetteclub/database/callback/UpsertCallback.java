package com.navetteclub.database.callback;

import java.util.List;

public interface UpsertCallback<T> {
    void onUpsertError();
    void onUpsertSuccess(List<T> items);
}

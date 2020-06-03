package com.navetteclub.ui;

import android.view.View;

public interface OnClickItemListener<T> {
    void onClick(View v, int position, T item);
}

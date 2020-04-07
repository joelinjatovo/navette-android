package com.navetteclub.ui;

import android.view.View;

import com.navetteclub.database.entity.OrderWithDatas;

public interface OnClickItemListener<T> {
    void onClick(View v, int position, T item);
}

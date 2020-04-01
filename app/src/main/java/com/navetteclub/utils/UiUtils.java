package com.navetteclub.utils;

import android.content.res.Resources;

public class UiUtils {

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}

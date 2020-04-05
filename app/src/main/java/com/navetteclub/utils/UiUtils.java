package com.navetteclub.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.navetteclub.R;
import com.navetteclub.database.entity.Club;
import com.navetteclub.views.MarkerView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UiUtils {

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static Bitmap getMarkerBitmapFromView(@NonNull Context context) {
        return  getMarkerBitmapFromView(context, null, null);
    }

    public static Bitmap getMarkerBitmapFromView(@NonNull Context context, String label) {
        return  getMarkerBitmapFromView(context, label, null);
    }

    public static Bitmap getMarkerBitmapFromView(@NonNull Context context, String label, Bitmap bitmap) {
        MarkerView customMarkerView = new MarkerView(context);
        if(label!=null){
            customMarkerView.getTitleView().setText(label);
        }

        if(bitmap!=null) {
            customMarkerView.getImageView().setImageBitmap(bitmap);
        }

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(
                customMarkerView.getMeasuredWidth(),
                customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888
            );
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }
}

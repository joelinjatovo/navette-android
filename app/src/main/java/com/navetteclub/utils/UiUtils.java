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

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.navetteclub.R;
import com.navetteclub.database.entity.Club;
import com.navetteclub.views.MarkerStepView;
import com.navetteclub.views.MarkerTextView;
import com.navetteclub.views.MarkerView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UiUtils {

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static BitmapDescriptor getBitmapFromMarkerView(@NonNull Context context, String label) {
        MarkerTextView customMarkerView = new MarkerTextView(context);

        if(label!=null){
            customMarkerView.getTitleView().setText(label);
        }

        return getBitmap(customMarkerView);
    }

    public static BitmapDescriptor getBitmapFromMarkerView(@NonNull Context context, String label, Bitmap bitmap) {
        MarkerView customMarkerView = new MarkerView(context);

        if(label!=null){
            customMarkerView.getTitleView().setText(label);
        }

        if(bitmap!=null) {
            customMarkerView.getImageView().setImageBitmap(bitmap);
        }

        return getBitmap(customMarkerView);
    }

    public static BitmapDescriptor getBitmapFromMarkerStepView(@NonNull Context context, String step, String title) {
        return getBitmapFromMarkerStepView(context, step, title, R.color.colorAccent);
    }

    public static BitmapDescriptor getBitmapFromMarkerStepView(@NonNull Context context, String step, String title, int color) {
        MarkerStepView customMarkerView = new MarkerStepView(context);

        if(step!=null){
            customMarkerView.getStepTextView().setText(step);
            customMarkerView.getStepTextView().setBackgroundResource(color);
        }

        if(title!=null){
            customMarkerView.getTitleTextView().setText(title);
        }

        return getBitmap(customMarkerView);
    }

    public static BitmapDescriptor getBitmapFromMarkerDotView(@NonNull Context context, int color) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.round_lens_black_18);
        imageView.setColorFilter(color);
        return getBitmap(imageView);
    }

    public static BitmapDescriptor getBitmapFromMarkerCarView(@NonNull Context context, int color) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.outline_directions_car_black_18);
        imageView.setColorFilter(color);
        return getBitmap(imageView);
    }

    private static BitmapDescriptor getBitmap(View view){
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(
                view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(returnedBitmap);
    }
}

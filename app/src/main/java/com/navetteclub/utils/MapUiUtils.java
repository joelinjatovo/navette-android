package com.navetteclub.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.navetteclub.database.entity.Point;

public class MapUiUtils {

    public static Marker drawTextPoint(Context context, GoogleMap map, Point point, String label) {
        if(map==null){
            return null;
        }

        LatLng latLng = point.toLatLng();
        MarkerOptions options = new MarkerOptions(); // Creating MarkerOptions
        options.position(latLng); // Setting the position of the marker
        options.icon(UiUtils.getBitmapFromMarkerView(context, label));

        return map.addMarker(options);
    }

    public static Marker drawStepPoint(Context context, GoogleMap map, Point point, String step, String label, int color) {
        if(map==null){
            return null;
        }

        LatLng latLng = point.toLatLng();
        MarkerOptions options = new MarkerOptions(); // Creating MarkerOptions
        options.position(latLng); // Setting the position of the marker
        options.icon(UiUtils.getBitmapFromMarkerStepView(context, step, label, color));

        return map.addMarker(options);
    }

    public static Marker drawStepPoint(Context context, GoogleMap map, Point point, String step, String label) {
        if(map==null){
            return null;
        }

        LatLng latLng = point.toLatLng();
        MarkerOptions options = new MarkerOptions(); // Creating MarkerOptions
        options.position(latLng); // Setting the position of the marker
        options.icon(UiUtils.getBitmapFromMarkerStepView(context, step, label));

        return map.addMarker(options);
    }

    public static Marker drawClubMarker(Context context, GoogleMap map, Point point, String label, Bitmap bitmap){
        LatLng latLng = point.toLatLng();
        MarkerOptions options = new MarkerOptions(); // Creating MarkerOptions
        options.position(latLng); // Setting the position of the marker
        options.icon(UiUtils.getBitmapFromMarkerView(context, label, bitmap));
        //options.anchor(0.5f, 1);
        return map.addMarker(options);
    }

    public static Marker drawDotMarker(Context context, GoogleMap map, Point point, int color){
        LatLng latLng = point.toLatLng();
        MarkerOptions options = new MarkerOptions(); // Creating MarkerOptions
        options.position(latLng); // Setting the position of the marker
        options.icon(UiUtils.getBitmapFromMarkerDotView(context, color));
        //options.anchor(0.5f, 1);
        return map.addMarker(options);
    }
}

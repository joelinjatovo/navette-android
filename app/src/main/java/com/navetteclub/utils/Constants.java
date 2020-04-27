package com.navetteclub.utils;

import com.google.android.gms.maps.model.LatLng;
import com.navetteclub.BuildConfig;

public final class Constants {

    public static final String AUTHORITY = "com.navetteclub";

    public static final LatLng DEFAULT_LOCATION = new LatLng(-18.915685, 47.539389);

    public static final float MAP_ZOOM = 15;

    private static final String API_KEY = "eyJpdiI6Imd2MTVZTC9QWXllaElDWlhta0JhVUE9PSIsInZhbHVlIjoiNlpzNG9KV1pTWktacUpzdmtPcTJUUT09IiwibWFjIjoiYmIwNTkzMTE2OTg3ODYyZmQ3YjE1MmE5YzE3ZjIzMTM0YTdjNTJhOTI4YmU1NWU2NDQxZTczNjNlNDdkYTY3MSJ9";
    private static final String API_KEY_DEV = "eyJpdiI6IkhlMTRnbnQzd0JFbXpMV0lTTkhxN3c9PSIsInZhbHVlIjoidHRzQ2dZTHdPcGQrQWtXSDhEZnRmdz09IiwibWFjIjoiODM2YTE0ZGNiOTMzMTVjZTZkMjg1MWMxNmVhZGQwYzU1MDk2ZGIxMWYzZDRkM2ZlNDdhMTY1OWY5MTZmZmJhOCJ9";
    public static String getApiKey(){
        if (BuildConfig.DEBUG) {
            return Constants.API_KEY_DEV;
        }
        return API_KEY;
    };

    //public static final String BASE_URL = "http://10.11.12.100:8000/"; // Work
    private static final String BASE_URL = "https://navetteclub.com/"; // Home
    private static final String BASE_URL_DEV = "http://192.168.43.6:8000/";
    public static String getBaseUrl(){
        if (BuildConfig.DEBUG) {
            return Constants.BASE_URL_DEV;
        }
        return BASE_URL;
    };

    private static final String STRIPE_API_KEY = "pk_test_TYooMQauvdEDq54NiTphI7jx";
    private static final String STRIPE_API_KEY_DEV = "pk_test_TYooMQauvdEDq54NiTphI7jx";
    public static String getStripeApiKey(){
        if (BuildConfig.DEBUG) {
            return Constants.STRIPE_API_KEY_DEV;
        }
        return STRIPE_API_KEY;
    };

    private static final String PUSHER_APP_ID = "959413";
    private static final String PUSHER_APP_CLUSTER = "eu";
    private static final String PUSHER_APP_KEY = "005a1b44bf7e4f2cef2d";
    private static final String PUSHER_APP_SECRET = "5929988339674bf54152";
    public static String getPusherAppCluster(){
        return PUSHER_APP_CLUSTER;
    };
    public static String getPusherAppKey(){
        return PUSHER_APP_KEY;
    };
}

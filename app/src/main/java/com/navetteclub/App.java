package com.navetteclub;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.multidex.MultiDexApplication;

import com.navetteclub.utils.Constants;
import com.stripe.android.PaymentConfiguration;

import java.io.File;

public class App extends MultiDexApplication {

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences googleBug = getSharedPreferences("google_bug_154855417", Context.MODE_PRIVATE);
        if (!googleBug.contains("fixed")) {
            File corruptedZoomTables = new File(getFilesDir(), "ZoomTables.data");
            corruptedZoomTables.delete();
            googleBug.edit().putBoolean("fixed", true).apply();
        }

        // Stripe
        PaymentConfiguration.init(
                getApplicationContext(),
                Constants.getStripeApiKey()
        );

        instance = this;


    }
}

package com.navetteclub;

import androidx.multidex.MultiDexApplication;

import com.navetteclub.utils.Constants;
import com.stripe.android.PaymentConfiguration;

public class App extends MultiDexApplication {

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Stripe
        PaymentConfiguration.init(
                getApplicationContext(),
                Constants.getStripeApiKey()
        );

        instance = this;


    }
}

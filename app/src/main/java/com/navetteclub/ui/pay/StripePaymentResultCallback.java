package com.navetteclub.ui.pay;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.navetteclub.ui.MainActivity;
import com.navetteclub.utils.Log;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.model.PaymentIntent;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class StripePaymentResultCallback implements ApiResultCallback<PaymentIntentResult> {

    private static final String TAG = StripePaymentResultCallback.class.getSimpleName();

    @NonNull
    private final WeakReference<MainActivity> activityRef;

    StripePaymentResultCallback(@NonNull MainActivity activity) {
        activityRef = new WeakReference<>(activity);
    }

    @Override
    public void onSuccess(@NonNull PaymentIntentResult result) {
        final MainActivity activity = activityRef.get();
        if (activity == null) {
            return;
        }

        PaymentIntent paymentIntent = result.getIntent();
        PaymentIntent.Status status = paymentIntent.getStatus();
        if (status == PaymentIntent.Status.Succeeded) {
            // Payment completed successfully
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Log.d(TAG, gson.toJson(paymentIntent));
        } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
            // Payment failed
            Log.e(TAG, Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage());
        }
    }

    @Override
    public void onError(@NonNull Exception e) {
        final MainActivity activity = activityRef.get();
        if (activity == null) {
            return;
        }

        // Payment request failed – allow retrying using the same payment method
        Log.e(TAG,e.getMessage(), e);
    }
}
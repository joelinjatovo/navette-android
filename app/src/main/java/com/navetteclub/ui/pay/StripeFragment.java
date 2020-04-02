package com.navetteclub.ui.pay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.navetteclub.R;
import com.navetteclub.databinding.FragmentStripeBinding;
import com.navetteclub.ui.MainActivity;
import com.navetteclub.ui.order.DetailFragment;
import com.navetteclub.utils.Log;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class StripeFragment extends Fragment {

    private static final String TAG = StripeFragment.class.getSimpleName();

    private FragmentStripeBinding mBinding;

    private String paymentIntentClientSecret;

    private Stripe stripe;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_stripe, container, false);

        return mBinding.getRoot();
    }


    private void startCheckout() {
        // Hook up the pay button to the card widget and stripe instance
        mBinding.payButton.setOnClickListener((View view) -> {
            PaymentMethodCreateParams params = mBinding.cardInputWidget.getPaymentMethodCreateParams();
            if (params != null) {
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                final Context context = requireContext();
                stripe = new Stripe(
                        context,
                        PaymentConfiguration.getInstance(context).getPublishableKey()
                );
                stripe.confirmPayment(this, confirmParams);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback((MainActivity) requireActivity()));
    }

    private static final class PaymentResultCallback implements ApiResultCallback<PaymentIntentResult> {
        @NonNull private final WeakReference<MainActivity> activityRef;

        PaymentResultCallback(@NonNull MainActivity activity) {
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
}

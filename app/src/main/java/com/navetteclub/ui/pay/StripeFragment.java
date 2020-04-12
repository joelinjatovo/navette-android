package com.navetteclub.ui.pay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.OrderParam;
import com.navetteclub.api.models.stripe.MyPaymentIntent;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.StripeApiService;
import com.navetteclub.databinding.FragmentStripeBinding;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;

public class StripeFragment extends Fragment {

    private static final String TAG = StripeFragment.class.getSimpleName();

    private FragmentStripeBinding mBinding;

    private String paymentIntentClientSecret;

    private Stripe stripe;

    private String token;

    private String orderRid;

    private OrderViewModel orderViewModel;

    private AuthViewModel authViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            // The getSearchType() method will be created automatically.
            token = StripeFragmentArgs.fromBundle(getArguments()).getToken();
            orderRid = StripeFragmentArgs.fromBundle(getArguments()).getOrder();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_stripe, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        setupAuthViewModel(factory);
        setupOrderViewModel(factory);
        setupUi();
        if(token!=null && orderRid!=null) {
            mBinding.setIsLoading(true);
            createPaymentIntent(token, orderRid);
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, paymentResultCallback);
    }

    private void setupUi() {
        mBinding.payButton.setOnClickListener(
                v -> {
                    payViaStripe();
                });
    }

    private void setupAuthViewModel(MyViewModelFactory factory) {
        authViewModel = new ViewModelProvider(this, factory).get(AuthViewModel.class);
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        mBinding.setIsUnauthenticated(false);
                    }else{
                        mBinding.setIsUnauthenticated(true);
                    }
                });
    }

    private void setupOrderViewModel(MyViewModelFactory factory) {
        orderViewModel = new ViewModelProvider(this, factory).get(OrderViewModel.class);
    }

    private void createPaymentIntent(String token, String orderId){
        Log.d(TAG, "service.createPaymentIntent(" + orderId + ")");
        StripeApiService service = RetrofitClient.getInstance().create(StripeApiService.class);
        retrofit2.Call<RetrofitResponse<MyPaymentIntent>> call = service.createPaymentIntent(token, new OrderParam(orderId));
        call.enqueue(new retrofit2.Callback<RetrofitResponse<MyPaymentIntent>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<MyPaymentIntent>> call,
                                   @NonNull Response<RetrofitResponse<MyPaymentIntent>> response) {
                Log.d(TAG, "responsee = " + response);
                mBinding.setIsLoading(false);
                if (!response.isSuccessful()) {
                    Log.e(TAG, response.toString());
                } else {
                    // The response from the server includes the Stripe publishable key and
                    // PaymentIntent details.
                    // For added security, our sample app gets the publishable key from the server
                    if (response.body() != null) {
                        Log.d(TAG, "response.body()= " + response.body());
                        if(response.body().isSuccess()){
                            if(response.body().getData()!=null){
                                String stripePublishableKey = response.body().getData().publishableKey;
                                paymentIntentClientSecret = response.body().getData().clientSecret;

                                // Configure the SDK with your Stripe publishable key so that it can make requests to the Stripe API
                                if(stripePublishableKey!=null){
                                    stripe = new Stripe(
                                            requireContext(),
                                            stripePublishableKey
                                    );
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<MyPaymentIntent>> call,
                                  @NonNull Throwable throwable) {
                mBinding.setIsLoading(false);
                Log.e(TAG, throwable.toString());

            }
        });
    }

    private void payViaStripe() {
        PaymentMethodCreateParams params = mBinding.stripeCardWidget.getPaymentMethodCreateParams();
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
    }

    private ApiResultCallback<PaymentIntentResult> paymentResultCallback = new ApiResultCallback<PaymentIntentResult>() {

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
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
            // Payment request failed – allow retrying using the same payment method
            Log.e(TAG,e.getMessage(), e);
        }
    };
}

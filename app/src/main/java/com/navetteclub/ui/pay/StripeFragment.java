package com.navetteclub.ui.pay;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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
import com.navetteclub.utils.UiUtils;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Response;

public class StripeFragment extends BottomSheetDialogFragment {

    private static final String TAG = StripeFragment.class.getSimpleName();

    private FragmentStripeBinding mBinding;

    private ProgressDialog progressDialog;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                ConstraintLayout constraintLayout = d.findViewById(R.id.constraintLayout);
                FrameLayout.LayoutParams params = null;
                if (constraintLayout != null) {
                    params = (FrameLayout.LayoutParams) constraintLayout.getLayoutParams();
                    params.height = UiUtils.getScreenHeight();
                    constraintLayout.setLayoutParams(params);
                }

                FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior sheetBehavior = BottomSheetBehavior.from(bottomSheet);
                    sheetBehavior.setSkipCollapsed(true);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    //sheetBehavior.setPeekHeight(UiUtils.getScreenHeight(), true);
                }
            }
        });

        return dialog;
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
            mBinding.setIsError(false);
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
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

        mBinding.payButton.setOnClickListener(
                v -> {
                    payViaStripe();
                });

        mBinding.loaderErrorView.getButton().setOnClickListener(
                v -> {
                    if(token!=null && orderRid!=null) {
                        mBinding.setIsLoading(true);
                        mBinding.setIsError(false);
                        createPaymentIntent(token, orderRid);
                    };
                });

        mBinding.authErrorView.getButton().setOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).navigate(R.id.navigation_auth);
                });

        mBinding.toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(StripeFragment.this).popBackStack());
    }

    private void setupAuthViewModel(MyViewModelFactory factory) {
        authViewModel = new ViewModelProvider(this, factory).get(AuthViewModel.class);
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        mBinding.setIsUnauthenticated(false);
                    }else{
                        mBinding.setIsUnauthenticated(true);
                        mBinding.setIsError(false);
                        mBinding.setIsLoading(false);
                    }
                });
    }

    private void setupOrderViewModel(MyViewModelFactory factory) {
        orderViewModel = new ViewModelProvider(this, factory).get(OrderViewModel.class);
    }

    private void createPaymentIntent(String token, String orderId){
        Log.d(TAG, "service.createPaymentIntent(" + orderId + ")");
        StripeApiService service = RetrofitClient.getInstance().create(StripeApiService.class);
        retrofit2.Call<RetrofitResponse<MyPaymentIntent>> call = service.pay(token, new OrderParam(orderId));
        call.enqueue(new retrofit2.Callback<RetrofitResponse<MyPaymentIntent>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<MyPaymentIntent>> call,
                                   @NonNull Response<RetrofitResponse<MyPaymentIntent>> response) {
                Log.d(TAG, "responsee = " + response);
                mBinding.setIsLoading(false);
                if (!response.isSuccessful()) {
                    Log.e(TAG, response.toString());
                    mBinding.setIsError(true);
                    mBinding.loaderErrorView.getTitleView().setText(getString(R.string.error));
                    mBinding.loaderErrorView.getSubtitleView().setText(response.message());
                } else {
                    // The response from the server includes the Stripe publishable key and
                    // PaymentIntent details.
                    // For added security, our sample app gets the publishable key from the server
                    if (response.body() != null) {
                        Log.d(TAG, "response.body()= " + response.body());
                        if(response.body().isSuccess()){
                            if(response.body().getData()!=null){
                                mBinding.setIsError(false);
                                String stripePublishableKey = response.body().getData().publishableKey;
                                paymentIntentClientSecret = response.body().getData().clientSecret;

                                // Configure the SDK with your Stripe publishable key so that it can make requests to the Stripe API
                                if(stripePublishableKey!=null){
                                    stripe = new Stripe(
                                            requireContext(),
                                            stripePublishableKey
                                    );
                                }
                            }else{
                                mBinding.setIsError(true);
                                mBinding.loaderErrorView.getTitleView().setText(getString(R.string.error));
                                mBinding.loaderErrorView.getSubtitleView().setText(response.body().getMessage());
                            }
                        }else{
                            mBinding.setIsError(true);
                            mBinding.loaderErrorView.getTitleView().setText(getString(R.string.error));
                            mBinding.loaderErrorView.getSubtitleView().setText(response.body().getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<MyPaymentIntent>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString());
                mBinding.setIsLoading(false);
                mBinding.setIsError(true);
                mBinding.loaderErrorView.getTitleView().setText(getString(R.string.error));
                mBinding.loaderErrorView.getSubtitleView().setText(throwable.getMessage());

            }
        });
    }

    private void payViaStripe() {
        progressDialog.show();
        PaymentMethodCreateParams params = mBinding.stripeCardWidget.getPaymentMethodCreateParams();
        if (params != null && stripe!=null) {
            ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                    .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
            stripe.confirmPayment(this, confirmParams);
        }
    }

    private ApiResultCallback<PaymentIntentResult> paymentResultCallback = new ApiResultCallback<PaymentIntentResult>() {

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            progressDialog.dismiss();

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Log.d(TAG, "Payment completed successfully");
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Log.d(TAG, gson.toJson(paymentIntent));
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed
                Log.d(TAG, "Payment failed");

                new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Payment failed")
                        .show();

                if(paymentIntent.getLastPaymentError()!=null) {
                    Log.e(TAG, paymentIntent.getLastPaymentError().getMessage());
                }
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            progressDialog.hide();
            new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText(e.getMessage())
                    .setConfirmText("Yes, retry!")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        payViaStripe();
                    })
                    .setCancelButton("Cancel", sDialog -> {
                        sDialog.dismissWithAnimation();
                        NavHostFragment.findNavController(StripeFragment.this).popBackStack();
                    })
                    .show();

            // Payment request failed – allow retrying using the same payment method
            Log.e(TAG, "Payment request failed – allow retrying using the same payment method", e);
        }
    };
}

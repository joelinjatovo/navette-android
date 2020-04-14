package com.navetteclub.ui.pay;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.navetteclub.R;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentCheckoutBinding;
import com.navetteclub.ui.order.DetailFragment;
import com.navetteclub.ui.order.OrderFragmentDirections;
import com.navetteclub.ui.order.SearchType;
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

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Map;
import java.util.Objects;

public class CheckoutFragment extends Fragment {

    private static final String TAG = CheckoutFragment.class.getSimpleName();

    private FragmentCheckoutBinding mBinding;

    private ProgressDialog progressDialog;

    private OrderViewModel orderViewModel;

    private AuthViewModel authViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_checkout, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        setupAuthViewModel(factory);
        setupOrderViewModel(factory);
        setupUi();
    }

    private void setupUi() {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

        mBinding.payPerStripeButton.setOnClickListener(
                v -> {
                    User user = authViewModel.getUser();
                    OrderWithDatas orderWithDatas = orderViewModel.getOrder();
                    Order order = null;
                    if(orderWithDatas!=null){
                        order = orderWithDatas.getOrder();
                    }
                    if(user!=null && order!=null){
                        CheckoutFragmentDirections.ActionCheckoutFragmentToStripeFragment action =
                                CheckoutFragmentDirections.actionCheckoutFragmentToStripeFragment(
                                        user.getAuthorizationToken(),
                                        order.getRid());
                        Navigation.findNavController(v).navigate(action);
                    }
                });

        mBinding.payPerCashButton.setOnClickListener(
                v -> {
                    if(authViewModel.getUser()!=null){
                        progressDialog.show();
                        orderViewModel.payPerCash(authViewModel.getUser());
                    }
                });

        mBinding.toolbar.setNavigationOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack();
                });
    }

    private void setupAuthViewModel(MyViewModelFactory factory) {
        authViewModel = new ViewModelProvider(this, factory).get(AuthViewModel.class);
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        boolean unauthenticated = false;
                        User user = authViewModel.getUser();
                        if(user!=null){
                            if(user.getPhone()==null){
                                unauthenticated = true;
                                mBinding.authErrorView.getTitleView().setText(R.string.error_phone);
                                mBinding.authErrorView.getSubtitleView().setText(R.string.error_phone_desc);
                            }else if(!user.getVerified()){
                                unauthenticated = true;
                                mBinding.authErrorView.getTitleView().setText(R.string.error_verify_phone);
                                mBinding.authErrorView.getSubtitleView().setText(R.string.error_verify_phone_desc);
                            }
                        }
                        mBinding.setIsUnauthenticated(unauthenticated);
                    }else{
                        mBinding.setIsUnauthenticated(true);
                        mBinding.authErrorView.getTitleView().setText(R.string.error);
                        mBinding.authErrorView.getSubtitleView().setText(R.string.error_401);
                    }
                });
    }

    private void setupOrderViewModel(MyViewModelFactory factory) {
        orderViewModel = new ViewModelProvider(this, factory).get(OrderViewModel.class);
        orderViewModel.getOrderLiveData().observe(getViewLifecycleOwner(),
                orderWithDatas -> {
                    if(orderWithDatas == null){
                        return;
                    }

                    if(orderWithDatas.getOrder()!=null){
                        Double amount = orderWithDatas.getOrder().getAmount();
                        if( amount != null && amount > 0 ){
                            String currency = orderWithDatas.getOrder().getCurrency();
                            NumberFormat format = NumberFormat.getCurrencyInstance();
                            //format.setMaximumFractionDigits(2);
                            //format.setMinimumFractionDigits(2);
                            format.setCurrency(Currency.getInstance(currency));
                            mBinding.setAmount(format.format(amount));
                        }

                        if( orderWithDatas.getOrder().getPaymentType() != null ) {
                            switch (orderWithDatas.getOrder().getPaymentType()) {
                                case Order.PAYMENT_TYPE_CASH:
                                case Order.PAYMENT_TYPE_APPLE_PAY:
                                case Order.PAYMENT_TYPE_STRIPE:
                                case Order.PAYMENT_TYPE_PAYPAL:
                                    NavHostFragment.findNavController(this).navigate(R.id.action_checkout_fragment_to_thanks_fragment);
                                    break;
                            }
                        }

                    }
                });

        orderViewModel.getOrderResult().observe(getViewLifecycleOwner(),
                orderResult -> {
                    if(orderResult == null){
                        return;
                    }

                    progressDialog.hide();

                    if (orderResult.getError() != null) {
                        Log.d(TAG, "'orderResult.getError()'");
                        Snackbar.make(mBinding.getRoot(), orderResult.getError(), Snackbar.LENGTH_SHORT).show();
                    }

                    if (orderResult.getSuccess() != null) {
                        Log.d(TAG, "'orderResult.getSuccess()'");
                        orderViewModel.setOrder(orderResult.getSuccess());
                    }
                });
    }
}

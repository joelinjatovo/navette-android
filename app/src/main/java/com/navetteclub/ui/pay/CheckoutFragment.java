package com.navetteclub.ui.pay;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.navetteclub.R;
import com.navetteclub.database.entity.Order;
import com.navetteclub.databinding.FragmentCheckoutBinding;
import com.navetteclub.ui.order.DetailFragment;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

import java.text.NumberFormat;
import java.util.Currency;

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

        setupUi();

        setupViewModel();
    }

    private void setupUi() {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

        mBinding.backButton.setOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack();
                });

        mBinding.confirmButton.setOnClickListener(
                v -> {
                    if(authViewModel.getUser()!=null){
                        progressDialog.show();
                        orderViewModel.pay(authViewModel.getUser(), Order.PAYMENT_TYPE_CASH);
                    }
                });
    }

    private void setupViewModel() {
        authViewModel = new ViewModelProvider(this,
                new MyViewModelFactory(requireActivity().getApplication())).get(AuthViewModel.class);

        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        mBinding.setIsUnauthenticated(false);
                    }else{
                        mBinding.setIsUnauthenticated(true);
                    }
                });

        orderViewModel = new ViewModelProvider(this, new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);

        orderViewModel.getOrderWithDatasLiveData().observe(getViewLifecycleOwner(),
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
                                    NavHostFragment.findNavController(this).navigate(R.id.global_to_thanks_fragment);
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
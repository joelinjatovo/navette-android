package com.navetteclub.ui.order;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.navetteclub.R;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.Point;
import com.navetteclub.databinding.FragmentDetailBinding;
import com.navetteclub.databinding.FragmentOrderViewBinding;
import com.navetteclub.utils.Log;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

import java.text.NumberFormat;
import java.util.Currency;

public class OrderViewFragment extends Fragment {

    private static final String TAG = OrderViewFragment.class.getSimpleName();

    private FragmentOrderViewBinding mBinding;

    private OrderViewModel orderViewModel;

    private AuthViewModel authViewModel;

    private ProgressDialog progressDialog;

    private Order order;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onCreateView");
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order_view, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG + "Cycle", "onViewCreated");

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

        setupUi();

        setupOrderViewModel();

        setupAuthViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG + "Cycle", "onDestroyView");
    }

    private void setupAuthViewModel() {
        authViewModel = new ViewModelProvider(this,
                new MyViewModelFactory(requireActivity().getApplication())).get(AuthViewModel.class);
    }

    private void setupOrderViewModel() {
        orderViewModel = new ViewModelProvider(this, new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);

        orderViewModel.getOrderWithDatasLiveData().observe(getViewLifecycleOwner(),
                orderWithDatas -> {
                    if(orderWithDatas == null){
                        return;
                    }

                    order = orderWithDatas.getOrder();
                    if(order!=null){
                        mBinding.setAmount(order.getAmountStr());

                        if(order.getPaymentType()!=null){
                            switch (order.getPaymentType()){
                                case Order.PAYMENT_TYPE_CASH:
                                    mBinding.espece.setChecked(true);
                                    break;
                                case Order.PAYMENT_TYPE_STRIPE:
                                    mBinding.card.setChecked(true);
                                    break;
                                case Order.PAYMENT_TYPE_PAYPAL:
                                    mBinding.paypal.setChecked(true);
                                    break;
                            }
                        }

                        if(order.getStatus()!=null){
                            switch (order.getStatus()){
                                case Order.STATUS_PING:
                                    mBinding.bookNowButton.setText(R.string.pay_now);
                                    break;
                                case Order.STATUS_OK:
                                    mBinding.bookNowButton.setText(R.string.view);
                                    break;
                                default:
                                    mBinding.bookNowButton.setText(R.string.book_now);
                                    break;
                            }
                        }
                    }


                    // Points
                    if(orderWithDatas.getPoints()!=null){
                        // Origin
                        if(orderWithDatas.getPoints().size()>0){
                            Point point = orderWithDatas.getPoints().get(0);
                            if(point!=null){
                                mBinding.setOrigin(point);
                            }
                        }

                        // Destination
                        if(orderWithDatas.getPoints().size()>1) {
                            Point point = orderWithDatas.getPoints().get(1);
                            if(point!=null) {
                                mBinding.setDestination(point);
                            }
                        }

                        // Retours
                        if(orderWithDatas.getPoints().size()>2) {
                            Point point = orderWithDatas.getPoints().get(2);
                            if(point!=null) {
                                mBinding.setRetours(point);
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

    private void setupUi() {
        mBinding.toolbar.setNavigationOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack();
                });

        mBinding.bookNowButton.setOnClickListener(
                v -> {
                    if(authViewModel.getUser()!=null && order != null && order.getRid() != null){
                        switch (order.getStatus()){
                            case Order.STATUS_OK:
                            case Order.STATUS_PROCESSING:
                                Navigation.findNavController(v).navigate(R.id.action_order_view_fragment_to_order_map_fragment);
                                break;
                            default:
                                // Go to checkout
                                Navigation.findNavController(v).navigate(R.id.action_order_fragment_to_navigation_checkout);
                                break;
                        }
                    }
                });
    }

}
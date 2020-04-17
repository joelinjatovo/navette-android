package com.navetteclub.ui.order;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.navetteclub.R;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentDetailBinding;
import com.navetteclub.databinding.FragmentOrderViewBinding;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.UiUtils;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

import java.text.NumberFormat;
import java.util.Currency;

public class OrderViewFragment extends BottomSheetDialogFragment {

    private static final String TAG = OrderViewFragment.class.getSimpleName();

    private FragmentOrderViewBinding mBinding;

    private OrderViewModel orderViewModel;

    private AuthViewModel authViewModel;

    private ProgressDialog progressDialog;

    private Order order;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                CoordinatorLayout coordinatorLayout = d.findViewById(R.id.coordinatorLayout);
                FrameLayout.LayoutParams params = null;
                if (coordinatorLayout != null) {
                    params = (FrameLayout.LayoutParams) coordinatorLayout.getLayoutParams();
                    params.height = UiUtils.getScreenHeight();
                    coordinatorLayout.setLayoutParams(params);
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
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        authViewModel = new ViewModelProvider(this, factory).get(AuthViewModel.class);
    }

    private void setupOrderViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        orderViewModel = new ViewModelProvider(this, factory).get(OrderViewModel.class);
        orderViewModel.getOrderLiveData().observe(getViewLifecycleOwner(),
                order1 -> {
                    setOrder(order1);
                });
    }

    private void setOrder(Order order1) {
        this.order = order1;
        if(order==null) return;

        mBinding.setOrderId(order.getRid());
        mBinding.setAmount(order.getAmountStr());

        if(order.getPaymentType()!=null){
            switch (order.getPaymentType()){
                case Order.PAYMENT_TYPE_CASH:
                    mBinding.editChip.setVisibility(View.VISIBLE);
                    mBinding.paymentMethods.setVisibility(View.VISIBLE);
                    mBinding.setPaymentType(getString(R.string.cash));
                    break;
                case Order.PAYMENT_TYPE_STRIPE:
                    mBinding.editChip.setVisibility(View.GONE);
                    mBinding.paymentMethods.setVisibility(View.VISIBLE);
                    mBinding.setPaymentType(getString(R.string.stripe));
                    break;
                case Order.PAYMENT_TYPE_PAYPAL:
                    mBinding.editChip.setVisibility(View.GONE);
                    mBinding.paymentMethods.setVisibility(View.VISIBLE);
                    mBinding.setPaymentType(getString(R.string.paypal));
                    break;
                default:
                    mBinding.paymentMethods.setVisibility(View.GONE);
                    break;
            }
        }else{
            mBinding.paymentMethods.setVisibility(View.GONE);
        }

        mBinding.cancelButton.setVisibility(Order.STATUS_OK.equals(order.getStatus())?View.VISIBLE:View.GONE);
        mBinding.liveButton.setVisibility(Order.STATUS_ACTIVE.equals(order.getStatus())?View.VISIBLE:View.GONE);
        if(order.getStatus()!=null){
            mBinding.setStatus(order.getStatus());
            mBinding.actionButton.setVisibility(Order.STATUS_PING.equals(order.getStatus())||Order.STATUS_ON_HOLD.equals(order.getStatus())?View.VISIBLE:View.GONE);
            mBinding.actionButton.setText(R.string.button_pay);
        }else{
            mBinding.actionButton.setVisibility(View.VISIBLE);
            mBinding.actionButton.setText(R.string.button_confirm);
        }
    }

    private void setupUi() {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

        NavController navController = NavHostFragment.findNavController(this);
        mBinding.toolbar.setNavigationOnClickListener(
                v -> {
                    navController.popBackStack();
                });
        mBinding.closeButton.setOnClickListener(
                v -> {
                    navController.popBackStack();
                });
        mBinding.cancelButton.setOnClickListener(
                v -> {
                    if(authViewModel.getUser()!=null && order != null && order.getRid() != null){
                        OrderViewFragmentDirections.ActionOrderViewFragmentToOrderCancelFragment action =
                                OrderViewFragmentDirections.actionOrderViewFragmentToOrderCancelFragment(
                                        authViewModel.getUser().getAuthorizationToken(),
                                        order.getRid()
                                );
                        navController.navigate(action);
                    }
                });
        mBinding.liveButton.setOnClickListener(
                v -> {
                    if(authViewModel.getUser()!=null && order != null && order.getRid() != null){
                        switch (order.getStatus()){
                            case Order.STATUS_OK:
                            case Order.STATUS_ACTIVE:
                                navController.navigate(R.id.action_order_view_fragment_to_live_fragment);
                                break;
                        }
                    }
                });
        mBinding.actionButton.setOnClickListener(
                v -> {
                    User user = authViewModel.getUser();
                    if(user!=null && order != null && order.getRid() != null){
                        if(order.getStatus()!=null){
                            switch (order.getStatus()){
                                case Order.STATUS_PING:
                                case Order.STATUS_ON_HOLD:
                                case Order.STATUS_PROCESSING:
                                    navController.navigate(R.id.action_order_view_fragment_to_navigation_checkout);
                                    break;
                                case Order.STATUS_OK:
                                    if(Order.PAYMENT_TYPE_CASH.equals(order.getPaymentType())){
                                        navController.navigate(R.id.action_order_view_fragment_to_navigation_checkout);
                                    }
                                    break;
                            }
                        }else{
                            orderViewModel.placeOrder(user.getAuthorizationToken());
                        }
                    }
                });

        /*
         * Go to checkout for PAYMENT_TYPE_CASH
         */
        mBinding.editChip.setOnClickListener(
                v -> {
                    if(authViewModel.getUser()!=null && order != null && order.getRid() != null){
                        if (Order.PAYMENT_TYPE_CASH.equals(order.getPaymentType())){
                            // Go to checkout
                            navController.navigate(R.id.action_order_view_fragment_to_navigation_checkout);
                        }
                    }
                });
    }

}

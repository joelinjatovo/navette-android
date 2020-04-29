package com.navetteclub.ui.pay;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.navetteclub.R;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentCheckoutBinding;
import com.navetteclub.utils.UiUtils;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CheckoutFragment extends BottomSheetDialogFragment {

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        setupAuthViewModel(factory);
        setupOrderViewModel(factory);
        setupUi();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    private void setupUi() {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

        mBinding.payPerStripeButton.setOnClickListener(
                v -> {
                    User user = authViewModel.getUser();
                    Order order = orderViewModel.getOrder();
                    if(user!=null && order!=null){
                        CheckoutFragmentDirections.ActionCheckoutFragmentToStripeFragment action =
                                CheckoutFragmentDirections.actionCheckoutFragmentToStripeFragment(
                                        user.getAuthorizationToken(),
                                        order.getRid());
                        NavHostFragment.findNavController(this).navigate(action);
                    }
                });

        mBinding.payPerCashButton.setOnClickListener(
                v -> {
                    payPerCash();
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
                order -> {
                    if(order == null){
                        return;
                    }
                    mBinding.setAmount(order.getTotalStr());

                    if(Order.PAYMENT_TYPE_CASH.equals(order.getPaymentType())){
                        mBinding.payPerCashButton.setVisibility(View.GONE);
                    }

                });
        orderViewModel.getPaymentResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null) return;
                    progressDialog.hide();
                    if(result.getError()!=null){
                        showError(result.getError());
                    }

                    if(result.getSuccess()!=null){
                        OrderWithDatas data = result.getSuccess();
                        if(data!=null){
                            NavHostFragment.findNavController(this).navigate(R.id.action_checkout_fragment_to_thanks_fragment);
                        }
                    }
                    orderViewModel.setPaymentResult(null);
                });
    }

    private void payPerCash() {
        User user = authViewModel.getUser();
        Order order = orderViewModel.getOrder();
        if(user!=null && order!=null && order.getRid()!=null){
            progressDialog.show();
            orderViewModel.payPerCash(user.getAuthorizationToken(), order.getRid());
        }
    }

    private void showError(Integer error) {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(getString(error))
                .setConfirmText("Yes, retry!")
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    payPerCash();
                })
                .setCancelButton("Cancel", sDialog -> {
                    sDialog.dismissWithAnimation();
                    NavHostFragment.findNavController(CheckoutFragment.this).popBackStack();
                })
                .show();
    }
}

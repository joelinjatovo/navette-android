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
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.navetteclub.database.entity.Car;
import com.navetteclub.database.entity.Club;
import com.navetteclub.database.entity.Item;
import com.navetteclub.database.entity.ItemWithDatas;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentDetailBinding;
import com.navetteclub.ui.pay.StripeFragment;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.UiUtils;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetailFragment extends BottomSheetDialogFragment {

    private static final String TAG = DetailFragment.class.getSimpleName();

    private FragmentDetailBinding mBinding;

    private OrderViewModel orderViewModel;

    private AuthViewModel authViewModel;

    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onCreateView");
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG + "Cycle", "onViewCreated");
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        setupOrderViewModel(factory);
        setupAuthViewModel(factory);
        setupUi();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG + "Cycle", "onDestroyView");
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
        orderViewModel.getOrderResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null) return;
                    progressDialog.hide();
                    if(result.getError()!=null){
                        showError(result.getError());
                    }

                    if(result.getSuccess()!=null){
                        OrderWithDatas data = result.getSuccess();
                        if(data!=null){
                            Order orderOnline = data.getOrder();
                            if(orderOnline!=null){
                                Car carOnline = data.getCar();
                                if(carOnline!=null){
                                    orderOnline.setCarId(carOnline.getId());
                                }
                                Club clubOnline = data.getClub();
                                if(clubOnline!=null){
                                    orderOnline.setClubId(clubOnline.getId());
                                }
                                orderViewModel.setOrderLiveData(orderOnline);
                            }
                            List<ItemWithDatas> items = data.getItems();
                            orderViewModel.setItemsLiveData(items);
                            NavHostFragment.findNavController(this).navigate(R.id.action_detail_fragment_to_navigation_checkout);
                        }
                    }
                    orderViewModel.setOrderResult(null);
                });
        orderViewModel.getOrderLiveData().observe(getViewLifecycleOwner(),
                order -> {
                    if(order==null) return;
                    if(order.getStatus()!=null){
                        switch (order.getStatus()){
                            case Order.STATUS_PING:
                            case Order.STATUS_ON_HOLD:
                            case Order.STATUS_PROCESSING:
                                mBinding.bookNowButton.setText(R.string.pay_now);
                                break;
                            case Order.STATUS_OK:
                                if(Order.PAYMENT_TYPE_CASH.equals(order.getPaymentType())) {
                                    mBinding.bookNowButton.setText(R.string.pay_now);
                                }else{
                                    mBinding.bookNowButton.setText(R.string.button_close);
                                }
                                break;
                            case Order.STATUS_ACTIVE:
                            case Order.STATUS_COMPLETED:
                            case Order.STATUS_CANCELED:
                                mBinding.bookNowButton.setText(R.string.button_close);
                            break;
                            default:
                                mBinding.bookNowButton.setText(R.string.book_now);
                            break;
                        }
                    }else{
                        mBinding.bookNowButton.setText(R.string.book_now);
                    }
                });
    }

    private void setupUi() {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));
        mBinding.bookNowButton.setOnClickListener(
                v -> {
                    Order order = orderViewModel.getOrder();
                    if(order==null) return;
                    if(order.getStatus()!=null){
                        switch (order.getStatus()){
                            case Order.STATUS_PING:
                            case Order.STATUS_ON_HOLD:
                            case Order.STATUS_PROCESSING:
                                // GO to checkout
                                NavHostFragment.findNavController(this)
                                        .navigate(R.id.action_detail_fragment_to_navigation_checkout);
                                break;
                            case Order.STATUS_OK:
                                if(Order.PAYMENT_TYPE_CASH.equals(order.getPaymentType())) {
                                    // GO to checkout
                                    NavHostFragment.findNavController(this)
                                            .navigate(R.id.action_detail_fragment_to_navigation_checkout);
                                }else{
                                    // GO back
                                    NavHostFragment.findNavController(this)
                                            .popBackStack();
                                }
                                break;
                            case Order.STATUS_ACTIVE:
                            case Order.STATUS_COMPLETED:
                            case Order.STATUS_CANCELED:
                                // GO back
                                NavHostFragment.findNavController(this)
                                        .popBackStack();
                                break;
                            default:
                                // Create order
                                placeOrder();
                                break;
                        }
                    }else{
                        // Create order
                        placeOrder();
                    }
                });
        mBinding.closeButton.setOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack();
                });
        mBinding.authErrorView.getButton().setOnClickListener(
                v -> {
                    User user = authViewModel.getUser();
                    NavController navController = Navigation.findNavController(v);
                    if(user==null){
                        navController.navigate(R.id.navigation_auth);
                    }else{
                        if(user.getPhone()==null){
                            navController.navigate(R.id.action_global_phone_fragment);
                        }else if(!user.getVerified()){
                            navController.navigate(R.id.action_global_verify_phone_fragment);
                        }
                    }
                });
    }

    private void placeOrder() {
        User user = authViewModel.getUser();
        if(user!=null){
            progressDialog.show();
            orderViewModel.placeOrder(user.getAuthorizationToken());
        }
    }

    private void showError(@StringRes Integer error) {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(getString(error))
                .setConfirmText("Yes, retry!")
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    placeOrder();
                })
                .setCancelButton("Cancel", sDialog -> {
                    sDialog.dismissWithAnimation();
                    NavHostFragment.findNavController(DetailFragment.this).popBackStack();
                })
                .show();
    }

}

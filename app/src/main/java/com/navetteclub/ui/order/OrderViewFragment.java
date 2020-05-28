package com.navetteclub.ui.order;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
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
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentOrderViewBinding;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.PusherOdk;
import com.navetteclub.utils.UiUtils;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OrderViewFragment extends BottomSheetDialogFragment {

    private static final String TAG = OrderViewFragment.class.getSimpleName();

    private FragmentOrderViewBinding mBinding;

    private OrderViewModel orderViewModel;

    private AuthViewModel authViewModel;

    private ProgressDialog progressDialog;

    private Order order;

    private String orderId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            orderId = OrderViewFragmentArgs.fromBundle(getArguments()).getOrderId();
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

                /*
                CoordinatorLayout coordinatorLayout = d.findViewById(R.id.coordinatorLayout);
                FrameLayout.LayoutParams params = null;
                if (coordinatorLayout != null) {
                    params = (FrameLayout.LayoutParams) coordinatorLayout.getLayoutParams();
                    params.height = UiUtils.getScreenHeight();
                    coordinatorLayout.setLayoutParams(params);
                }
                */

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
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        String token = authViewModel.getUser().getAuthorizationToken();
                        progressDialog.show();
                        orderViewModel.getOrder(token, orderId);
                    }
                });
    }

    private void setupOrderViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        orderViewModel = new ViewModelProvider(this, factory).get(OrderViewModel.class);
        orderViewModel.getViewResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){return;}
                    progressDialog.dismiss();
                    if(result.getError()!=null){
                        Toast.makeText(requireContext(), result.getError(), Toast.LENGTH_SHORT).show();
                    }

                    if(result.getSuccess()!=null){
                        orderViewModel.attach(result.getSuccess());
                    }

                    orderViewModel.setViewResult(null);
                });

        orderViewModel.getOrderLiveData().observe(getViewLifecycleOwner(), this::setOrder);
        orderViewModel.getClubLiveData().observe(getViewLifecycleOwner(),
                club -> {
                    if(club!=null){
                        // Aller
                        mBinding.setPoint2Title(getString(R.string.club));
                        mBinding.setPoint2(club.getName());

                        // Retours
                        mBinding.setPoint3Title(getString(R.string.club));
                        mBinding.setPoint3(club.getName());
                    }
                });
        orderViewModel.getItem1PointLiveData().observe(getViewLifecycleOwner(),
                point -> {
                    if(point!=null){
                        Item item1 = orderViewModel.getItem1();
                        if(item1!=null){
                            mBinding.setItem1Id(item1.getRid());
                            if(Order.TYPE_BACK.equals(item1.getType())){
                                mBinding.setStatus2(getString(item1.getStatusRes()));
                                mBinding.setPoint4Title(getString(R.string.ride_drop));
                                mBinding.setPoint4(point.getName());
                                mBinding.setDuration2(item1.getDuration());
                                mBinding.setDistance2(item1.getDistance());
                                mBinding.setDate2(getDateString(item1.getRideAt()));
                            }else{
                                mBinding.setStatus1(getString(item1.getStatusRes()));
                                mBinding.setPoint1Title(getString(R.string.ride_pickup));
                                mBinding.setPoint1(point.getName());
                                mBinding.setDuration1(item1.getDuration());
                                mBinding.setDistance1(item1.getDistance());
                                mBinding.setDate1(getDateString(item1.getRideAt()));
                            }
                        }
                    }
                });
        orderViewModel.getItem2PointLiveData().observe(getViewLifecycleOwner(),
                point -> {
                    if(point!=null){
                        Item item2 = orderViewModel.getItem2();
                        if(item2!=null){
                            mBinding.setStatus2(getString(item2.getStatusRes()));
                            mBinding.setItem2Id(item2.getRid());
                            mBinding.setPoint4Title(getString(R.string.ride_drop));
                            mBinding.setPoint4(point.getName());
                            mBinding.setDuration2(item2.getDuration());
                            mBinding.setDistance2(item2.getDistance());
                            mBinding.setDate2(getDateString(item2.getRideAt()));
                        }
                    }
                });
        orderViewModel.getOrderResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null) return;
                    progressDialog.dismiss();
                    if(result.getError()!=null){
                        showError(result.getError());
                    }

                    if(result.getSuccess()!=null){
                        OrderWithDatas data = result.getSuccess();
                        if(data!=null){
                            Order orderOnline = data.getOrder();
                            if(orderOnline!=null){
                                setOrder(orderOnline);
                            }
                        }
                    }
                    orderViewModel.setOrderResult(null);
                });
    }

    private String getDateString(Date lastUpdated){
        if(lastUpdated==null) return null;
        long now = System.currentTimeMillis();
        CharSequence date = DateUtils.getRelativeTimeSpanString(lastUpdated.getTime(), now, DateUtils.MINUTE_IN_MILLIS);
        return (String) date;
    }

    private void setOrder(Order order1) {
        this.order = order1;
        if(order==null) return;

        mBinding.setOrderId(order.getRid());
        mBinding.setSubtotal(order.getSubtotalStr());
        mBinding.setTotal(order.getTotalStr());
        mBinding.setAmount(order.getAmountStr());
        mBinding.setPlace(order.getPlace());
        mBinding.setCoefficient(String.valueOf(order.getCoefficient()));

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

        mBinding.cancelButton.setVisibility(View.GONE);
        mBinding.actionButton.setVisibility(View.GONE);
        if(order.getStatus()!=null){
            switch (order.getStatus()){
                case Order.STATUS_PING:
                    mBinding.setStatus(getString(R.string.status_ping));
                    mBinding.toolbar.setSubtitleTextColor(getResources().getColor(R.color.gray));
                    mBinding.actionButton.setVisibility(View.VISIBLE);
                    break;
                case Order.STATUS_ON_HOLD:
                    mBinding.setStatus(getString(R.string.status_on_hold));
                    mBinding.toolbar.setSubtitleTextColor(getResources().getColor(R.color.gray));
                    mBinding.actionButton.setVisibility(View.VISIBLE);
                    break;
                case Order.STATUS_OK:
                    mBinding.setStatus(getString(R.string.status_ok));
                    mBinding.cancelButton.setVisibility(View.VISIBLE);
                    mBinding.toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorAccent));
                break;
                case Order.STATUS_ACTIVE:
                    mBinding.setStatus(getString(R.string.status_active));
                    mBinding.toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorAlert));
                break;
                case Order.STATUS_CANCELED:
                    mBinding.setStatus(getString(R.string.status_canceled));
                    mBinding.editChip.setVisibility(View.GONE);
                    mBinding.toolbar.setSubtitleTextColor(getResources().getColor(R.color.gray));
                    break;
                case Order.STATUS_COMPLETED:
                    mBinding.setStatus(getString(R.string.status_completed));
                    mBinding.editChip.setVisibility(View.GONE);
                    mBinding.toolbar.setSubtitleTextColor(getResources().getColor(R.color.gray));
                    break;
            }
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
        mBinding.toolbar.setNavigationOnClickListener(v -> navController.popBackStack());
        mBinding.cancelButton.setOnClickListener(
                v -> {
                    if(authViewModel.getUser()!=null && order != null && order.getRid() != null){
                        OrderViewFragmentDirections.ActionOrderViewFragmentToOrderCancelFragment action =
                                OrderViewFragmentDirections.actionOrderViewFragmentToOrderCancelFragment(
                                        order.getRid()
                                );
                        navController.navigate(action);
                    }
                });
        mBinding.live1Button.setOnClickListener(
                v -> {
                    User user = authViewModel.getUser();
                    if(user!=null && user.getAuthorizationToken() != null && mBinding.getItem1Id()!=null){
                        OrderViewFragmentDirections.ActionOrderViewFragmentToLiveFragment action =
                                OrderViewFragmentDirections.actionOrderViewFragmentToLiveFragment(
                                        mBinding.getItem1Id()
                                );
                        navController.navigate(action);
                    }
                });
        mBinding.live2Button.setOnClickListener(
                v -> {
                    User user = authViewModel.getUser();
                    if(user!=null && user.getAuthorizationToken() != null && mBinding.getItem2Id()!=null){
                        OrderViewFragmentDirections.ActionOrderViewFragmentToLiveFragment action =
                                OrderViewFragmentDirections.actionOrderViewFragmentToLiveFragment(
                                        mBinding.getItem2Id()
                                );
                        navController.navigate(action);
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

    private void showError(@StringRes Integer error) {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.oops))
                .setContentText(getString(error))
                .setConfirmText(getString(R.string.button_retry))
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    placeOrder();
                })
                .setCancelButton(getString(R.string.button_cancel), sDialog -> {
                    sDialog.dismissWithAnimation();
                    NavHostFragment.findNavController(OrderViewFragment.this).popBackStack();
                })
                .show();
    }

    private void placeOrder() {
        User user = authViewModel.getUser();
        if(user!=null && order != null && order.getRid() != null) {
            orderViewModel.placeOrder(user.getAuthorizationToken());
        }
    }

    public static Uri getUri(String orderId){
        return Uri.parse("http://navetteclub.com/order/" + orderId);
    }

}

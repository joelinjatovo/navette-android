package com.navetteclub.ui.order;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.navetteclub.R;
import com.navetteclub.database.entity.Car;
import com.navetteclub.database.entity.Club;
import com.navetteclub.database.entity.Item;
import com.navetteclub.database.entity.ItemWithDatas;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentCartBinding;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.UiUtils;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CartFragment extends BottomSheetDialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = CartFragment.class.getSimpleName();

    private FragmentCartBinding mBinding;

    private OrderViewModel orderViewModel;

    private AuthViewModel authViewModel;

    private ProgressDialog progressDialog;

    private Calendar calendar;

    private View dateTimeView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onCreateView");
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false);

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
        loadCart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG + "Cycle", "onDestroyView");
    }

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getChildFragmentManager().findFragmentByTag("Datepickerdialog");
        TimePickerDialog tpd = (TimePickerDialog) getChildFragmentManager().findFragmentByTag("TimepickerDialog");
        if(tpd != null) tpd.setOnTimeSetListener(this);
        if(dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = "You picked the following time: "+hourOfDay+"h"+minute+"m"+second;
        Log.d(TAG, time);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        if(dateTimeView!=null) {
            if (dateTimeView.getId() == R.id.date1Layout) {
                Item item = orderViewModel.getItem1();
                if(item==null){
                    item = new Item();
                }
                item.setRideAt(calendar.getTime());
                orderViewModel.setItem1LiveData(item);
            } else if (dateTimeView.getId() == R.id.date2Layout) {
                Item item = orderViewModel.getItem2();
                if(item==null){
                    item = new Item();
                }
                item.setRideAt(calendar.getTime());
                orderViewModel.setItem2LiveData(item);
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        Log.d(TAG, date);
        if(calendar==null){
            calendar = Calendar.getInstance();
        }
        calendar.set(year, monthOfYear, dayOfMonth);

        //dateTextView.setText(date);
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd =TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.dismissOnPause(true);
        tpd.setVersion(TimePickerDialog.Version.VERSION_2);
        tpd.show(getChildFragmentManager(), "Timepickerdialog");
    }

    private void loadCart() {
        Club club = orderViewModel.getClub();
        if(club==null) return;
        Item item = orderViewModel.getItem1();
        if(item==null) return;
        Order order = orderViewModel.getOrder();
        if(order==null || order.getRid()==null){
            mBinding.setIsLoading(true);
            mBinding.setShowErrorLoader(false);
            orderViewModel.getCart();
        }
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
        orderViewModel.getCartResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null) return;

                    mBinding.setIsLoading(false);

                    if(result.getError()!=null){
                        mBinding.setShowErrorLoader(true);
                        mBinding.loaderErrorView.getSubtitleView().setText(result.getError());
                    }

                    if(result.getSuccess()!=null){
                        OrderWithDatas data = result.getSuccess();
                        if(data!=null){
                            Order order = data.getOrder();
                            if(order!=null) {
                                Order orderLive = orderViewModel.getOrder();
                                if(orderLive!=null) {
                                    orderLive.setSubtotal(order.getSubtotal());
                                    orderLive.setTotal(order.getTotal());
                                    orderLive.setAmount(order.getAmount());
                                }
                                orderViewModel.setOrderLiveData(orderLive);
                            }
                        }
                    }
                });
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
                            if(Order.TYPE_BACK.equals(item1.getType())){
                                mBinding.setPoint4(point.getName());
                            }else{
                                mBinding.setPoint1(point.getName());
                            }
                        }
                    }
                });
        orderViewModel.getItem1LiveData().observe(getViewLifecycleOwner(),
                item1 -> {
                    if(item1!=null){
                        if(Order.TYPE_BACK.equals(item1.getType())){
                            mBinding.setPoint4Title("Drop");
                            mBinding.setDuration2(item1.getDuration());
                            mBinding.setDistance2(item1.getDistance());
                            mBinding.setDate2(getDateString(item1.getRideAt()));
                        }else{
                            mBinding.setPoint1Title("Pickup");
                            mBinding.setDuration1(item1.getDuration());
                            mBinding.setDistance1(item1.getDistance());
                            mBinding.setDate1(getDateString(item1.getRideAt()));
                        }
                    }
                });
        orderViewModel.getItem2PointLiveData().observe(getViewLifecycleOwner(),
                point -> {
                    if(point!=null){
                        Item item2 = orderViewModel.getItem2();
                        if(item2!=null){
                            mBinding.setPoint4(point.getName());
                        }
                    }
                });
        orderViewModel.getItem2LiveData().observe(getViewLifecycleOwner(),
                item2 -> {
                    if(item2!=null){
                        mBinding.setPoint4Title(getString(R.string.ride_drop));
                        mBinding.setDuration2(item2.getDuration());
                        mBinding.setDistance2(item2.getDistance());
                        mBinding.setDate2(getDateString(item2.getRideAt()));
                    }
                });
        orderViewModel.getOrderResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null) return;
                    progressDialog.hide();
                    if(result.getError()!=null){
                        showOrderError(result.getError());
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
                            NavHostFragment.findNavController(this).navigate(R.id.action_cart_fragment_to_navigation_checkout);
                        }
                    }
                    orderViewModel.setOrderResult(null);
                });
        orderViewModel.getOrderLiveData().observe(getViewLifecycleOwner(),
                order -> {
                    if(order==null) return;
                    updateUiWithOrderData(order);
                });
    }

    private String getDateString(Date lastUpdated) {
        if (lastUpdated == null) return getString(R.string.now);
        long now = System.currentTimeMillis();
        CharSequence date = DateUtils.getRelativeTimeSpanString(lastUpdated.getTime(), now, DateUtils.MINUTE_IN_MILLIS);
        return (String) date;
    }

    private void updateUiWithOrderData(Order order) {
        if(order==null) return;

        mBinding.setOrderId(order.getRid());
        mBinding.setSubtotal(order.getSubtotalStr());
        mBinding.setTotal(order.getTotalStr());
        mBinding.setPlace(order.getPlace());

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
                                        .navigate(R.id.action_cart_fragment_to_navigation_checkout);
                                break;
                            case Order.STATUS_OK:
                                if(Order.PAYMENT_TYPE_CASH.equals(order.getPaymentType())) {
                                    // GO to checkout
                                    NavHostFragment.findNavController(this)
                                            .navigate(R.id.action_cart_fragment_to_navigation_checkout);
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
        mBinding.date1Layout.setOnClickListener(
                v -> {
                    dateTimeView = v;
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            this,
                            now.get(Calendar.YEAR), // Initial year selection
                            now.get(Calendar.MONTH), // Initial month selection
                            now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                    );
                    Calendar calendar = Calendar.getInstance();
                    dpd.setMinDate(calendar);
                    calendar.roll(Calendar.MONTH, 2);
                    dpd.setMaxDate(calendar);
                    dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                    dpd.dismissOnPause(true);
                    dpd.show(getChildFragmentManager(), "Datepickerdialog");
                });
        mBinding.date2Layout.setOnClickListener(
                v -> {
                    dateTimeView = v;
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            this,
                            now.get(Calendar.YEAR), // Initial year selection
                            now.get(Calendar.MONTH), // Initial month selection
                            now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                    );
                    Calendar calendar = Calendar.getInstance();
                    dpd.setMinDate(calendar);
                    calendar.roll(Calendar.MONTH, 2);
                    dpd.setMaxDate(calendar);
                    dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                    dpd.dismissOnPause(true);
                    dpd.show(getChildFragmentManager(), "Datepickerdialog");
                });
        mBinding.authErrorView.getButton().setOnClickListener(
                v -> {
                    User user = authViewModel.getUser();
                    NavController navController = NavHostFragment.findNavController(this);
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
        mBinding.loaderErrorView.getButton().setOnClickListener(
                v -> {
                    loadCart();
                });
    }

    private void placeOrder() {
        User user = authViewModel.getUser();
        if(user!=null){
            progressDialog.show();
            orderViewModel.placeOrder(user.getAuthorizationToken());
        }
    }

    private void showCartError(@StringRes Integer error) {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.oops))
                .setContentText(getString(error))
                .setConfirmText(getString(R.string.button_retry))
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    loadCart();
                })
                .setCancelButton(getString(R.string.button_cancel), sDialog -> {
                    sDialog.dismissWithAnimation();
                    NavHostFragment.findNavController(CartFragment.this).popBackStack();
                })
                .show();
    }

    private void showOrderError(@StringRes Integer error) {
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
                    NavHostFragment.findNavController(CartFragment.this).popBackStack();
                })
                .show();
    }

}

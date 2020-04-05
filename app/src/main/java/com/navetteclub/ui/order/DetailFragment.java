package com.navetteclub.ui.order;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.navetteclub.R;
import com.navetteclub.api.models.google.GoogleDirectionResponse;
import com.navetteclub.api.models.google.Leg;
import com.navetteclub.api.models.google.Route;
import com.navetteclub.api.services.GoogleApiService;
import com.navetteclub.database.entity.CarAndModel;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentDetailBinding;
import com.navetteclub.databinding.FragmentOrderBinding;
import com.navetteclub.databinding.FragmentTravelBinding;
import com.navetteclub.utils.Constants;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.Utils;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;
import com.stripe.android.Stripe;
import com.stripe.android.model.PaymentIntent;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailFragment extends Fragment {

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

        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        mBinding.setIsUnauthenticated(false);
                    }else{
                        mBinding.setIsUnauthenticated(true);
                    }
                });
    }

    private void setupOrderViewModel() {
        orderViewModel = new ViewModelProvider(this,
                new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);

        orderViewModel.getOrderLiveData().observe(getViewLifecycleOwner(),
                orderWithDatas -> {
                    if(orderWithDatas == null){
                        return;
                    }

                    if(orderWithDatas.getOrder()!=null){
                        mBinding.setDistance(orderWithDatas.getOrder().getDistance());

                        mBinding.setDelay(orderWithDatas.getOrder().getDelay());

                        Double amount = orderWithDatas.getOrder().getAmount();
                        if( amount != null && amount > 0 ){
                            mBinding.setAmount(orderWithDatas.getOrder().getAmountStr());

                            switch (orderWithDatas.getOrder().getStatus()){
                                case Order.STATUS_PING:
                                    mBinding.bookNowButton.setText(R.string.pay_now);
                                    NavHostFragment.findNavController(this).navigate(R.id.navigation_checkout);
                                break;
                                case Order.STATUS_OK:
                                    mBinding.bookNowButton.setText(R.string.view);
                                    NavHostFragment.findNavController(this).navigate(R.id.global_to_thanks_fragment);
                                break;
                                default:
                                    mBinding.bookNowButton.setText(R.string.book_now);
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


    private void setupUi() {
        mBinding.stepView.go(5, true);

        mBinding.bookNowButton.setOnClickListener(
                v -> {
                    if(authViewModel.getUser()!=null
                            && orderViewModel.getOrder() != null
                            && orderViewModel.getOrder().getOrder() != null
                            && orderViewModel.getOrder().getOrder().getRid() == null){
                        progressDialog.show();

                        orderViewModel.placeOrder(authViewModel.getUser());
                    }
                });

        mBinding.authErrorView.getButton().setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.navigation_auth);
                });
    }

}

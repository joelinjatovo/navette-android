package com.navetteclub.ui.driver;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.R;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.Ride;
import com.navetteclub.database.entity.RidePointWithDatas;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentRidePointBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.ui.order.SearchFragmentArgs;
import com.navetteclub.ui.pay.StripeFragment;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.RidesViewModel;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RidePointFragment extends Fragment {

    private static final String TAG = RidePointFragment.class.getSimpleName();

    private FragmentRidePointBinding mBinding;

    private ProgressDialog progressDialog;

    private RidePointRecyclerViewAdapter mAdapter;

    private AuthViewModel authViewModel;

    private RidesViewModel ridesViewModel;

    private SearchView searchView;

    private String token;

    private Long rideId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            token = RidePointFragmentArgs.fromBundle(getArguments()).getToken();
            rideId = RidePointFragmentArgs.fromBundle(getArguments()).getRideId();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ride_point, container, false);

        mAdapter = new RidePointRecyclerViewAdapter(mListener, mCallListener);
        RecyclerView recyclerView = mBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
        setupAuthViewModel();
        setupRidesViewModel();
    }

    private void setupRidesViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        ridesViewModel = new ViewModelProvider(requireActivity(), factory).get(RidesViewModel.class);
        ridesViewModel.getPointsLiveData().observe(getViewLifecycleOwner(),
                points -> {
                    if(points==null){
                        return;
                    }
                    mBinding.setIsLoading(false);
                    if(points.getError()!=null){
                        mBinding.setShowError(true);
                        mBinding.loaderErrorView.getSubtitleView().setText(points.getError());
                    }
                    if(points.getSuccess()!=null){
                        mBinding.setShowError(false);
                        mAdapter.setItems(points.getSuccess());
                    }

                    //ridesViewModel.setPointsLiveData(null);

                });

        ridesViewModel.getRideLiveData().observe(getViewLifecycleOwner(),
                ride -> {
                    if(ride==null){
                        return;
                    }
                    progressDialog.hide();

                    if(ride.getError()!=null){
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText(getString(ride.getError()))
                                .show();
                    }

                    if(ride.getSuccess()!=null){
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setContentText("Votre course a bien commencé!")
                                .setConfirmText("Yes, go to live")
                                .setConfirmClickListener(sDialog -> {
                                    sDialog.dismissWithAnimation();
                                    NavHostFragment.findNavController(RidePointFragment.this).navigate(R.id.action_ride_point_fragment_to_ride_map_fragment);
                                })
                                .setCancelButton("Ok", SweetAlertDialog::dismissWithAnimation)
                                .show();
                    }

                    ridesViewModel.setRideLiveData(null);
                });
    }

    private void setupAuthViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        ridesViewModel.loadPoints(token, rideId);
                        mBinding.setIsLoading(true);
                        mBinding.setShowError(false);
                        mBinding.setIsUnauthenticated(false);
                    }else{
                        mBinding.setIsLoading(false);
                        mBinding.setShowError(false);
                        mBinding.setIsUnauthenticated(true);
                    }
                });
    }

    private void setupUI() {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

        mBinding.toolbar.setNavigationOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).popBackStack();
                });

        mBinding.startRideButton.setOnClickListener(v->{
            if(token!=null && rideId!=null){
                progressDialog.show();
                ridesViewModel.start(token, rideId);
            }
        });

        mBinding.loaderErrorView.getButton().setOnClickListener(
                v -> {
                    User user = authViewModel.getUser();
                    if(user!=null){
                        ridesViewModel.loadPoints(token, rideId);
                        mBinding.setIsLoading(true);
                        mBinding.setShowError(false);
                    }else{
                        mBinding.setIsLoading(false);
                    }
                });

        mBinding.authErrorView.getButton().setOnClickListener(
                v -> {
                    //Navigation.findNavController(v).navigate(R.id.action_orders_fragment_to_navigation_auth);
                });
    }

    private void onCallBtnClick(String phone){
        if (Build.VERSION.SDK_INT < 23) {
            phoneCall(phone);
        }else {

            if (ActivityCompat.checkSelfPermission(requireActivity(),
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                phoneCall(phone);
            }else {
                final String[] PERMISSIONS_STORAGE = {Manifest.permission.CALL_PHONE};
                //Asking request Permissions
                ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS_STORAGE, 9);
            }
        }
    }

    private void phoneCall(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionGranted = false;
        switch(requestCode){
            case 9:
                permissionGranted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                break;
        }
        if(!permissionGranted){
            Toast.makeText(requireActivity(), "You don't assign permission.", Toast.LENGTH_SHORT).show();
        }
    }

    private OnClickItemListener<RidePointWithDatas> mListener = (v, pos, item) -> {
        //NavHostFragment.findNavController(RideFragment.this).navigate(R.id.action_rides_fragment_to_ride_fragment);
    };

    private OnClickItemListener<RidePointWithDatas> mCallListener = (v, pos, item) -> {
        if (item.getUser() != null && item.getUser().getPhone() != null) {
            onCallBtnClick(item.getUser().getPhone());
        }
    };

}

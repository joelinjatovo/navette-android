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
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.navetteclub.R;
import com.navetteclub.database.entity.Ride;
import com.navetteclub.database.entity.RidePoint;
import com.navetteclub.databinding.FragmentRidePointsBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.RideViewModel;
import com.navetteclub.vm.RidesViewModel;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RidePointsFragment extends Fragment {

    private static final String TAG = RidePointsFragment.class.getSimpleName();

    private FragmentRidePointsBinding mBinding;

    private ProgressDialog progressDialog;

    private RidePointRecyclerViewAdapter mAdapter;

    private AuthViewModel authViewModel;

    private RidesViewModel ridesViewModel;

    private SearchView searchView;

    private String token;

    private Long rideId;

    private Ride rideWithDatas;

    private RideViewModel rideViewModel;

    private View.OnClickListener mActualiseListener;
    private View.OnClickListener mCancelListener;
    private View.OnClickListener mActiveListener;
    private View.OnClickListener mCompleteListener;
    private View.OnClickListener mLiveListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            rideId = RidePointsFragmentArgs.fromBundle(getArguments()).getRideId();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ride_points, container, false);

        mAdapter = new RidePointRecyclerViewAdapter(mListener);
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
        setupRideViewModel();
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

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    private void setupAuthViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        token = authViewModel.getUser().getAuthorizationToken();
                        loadRide();
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

    private void setupRidesViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        ridesViewModel = new ViewModelProvider(requireActivity(), factory).get(RidesViewModel.class);
        ridesViewModel.getRideResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }
                    mBinding.setIsLoading(false);
                    if(result.getError()!=null){
                        mBinding.setShowError(true);
                        mBinding.loaderErrorView.getSubtitleView().setText(result.getError());
                    }
                    if(result.getSuccess()!=null){
                        mBinding.setShowError(false);
                        setRide(result.getSuccess());
                    }
                    ridesViewModel.setRideResult(null);
                });

        ridesViewModel.getRideDirectionResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }
                    progressDialog.dismiss();

                    if(result.getError()!=null){
                        showSweetError(getString(result.getError()));
                    }

                    if(result.getSuccess()!=null){
                        this.setRide(result.getSuccess());
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setContentText("Votre course est actualisé!")
                                .show();
                    }

                    ridesViewModel.setRideDirectionResult(null);
                });

        ridesViewModel.getRideStartResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }
                    progressDialog.dismiss();

                    if(result.getError()!=null){
                        showSweetError(getString(result.getError()));
                    }

                    if(result.getSuccess()!=null){
                        this.setRide(result.getSuccess());
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setContentText("Votre course a bien commencé!")
                                .show();
                    }

                    ridesViewModel.setRideStartResult(null);
                });

        ridesViewModel.getRideCancelResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }
                    progressDialog.dismiss();

                    if(result.getError()!=null){
                        showSweetError(getString(result.getError()));
                    }

                    if(result.getSuccess()!=null){
                        this.setRide(result.getSuccess());
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setContentText("Votre course a été bien annulé!")
                                .show();
                    }

                    ridesViewModel.setRideCancelResult(null);
                });

        ridesViewModel.getRideCompleteResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }
                    progressDialog.hide();

                    if(result.getError()!=null){
                        showSweetError(getString(result.getError()));
                    }

                    if(result.getSuccess()!=null){
                        this.setRide(result.getSuccess());
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setContentText("Votre course a terminé!")
                                .show();
                    }

                    ridesViewModel.setRideCompleteResult(null);
                });
    }

    private void setupRideViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        rideViewModel = new ViewModelProvider(requireActivity(), factory).get(RideViewModel.class);
        rideViewModel.getRideCancelResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }
                    progressDialog.hide();
                    if(result.getError()!=null){
                        if(result.getError() == R.string.error_401) { // Error 401: Unauthorized
                            authViewModel.logout(requireContext());
                        }
                    }
                    if(result.getSuccess()!=null){
                        setRide(result.getSuccess());
                    }
                    rideViewModel.setRideCancelResult(null);
                });
    }

    private void showSweetError(String string) {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(string)
                .show();
    }

    private void loadRide() {
        ridesViewModel.loadRide(token, rideId);
    }

    private void setRide(Ride rideWithDatas) {
        this.rideWithDatas = rideWithDatas;
        if(rideWithDatas==null) return;

        // Ride
        Ride ride = rideWithDatas;
        if(ride!=null){
            if(Ride.STATUS_CANCELABLE.equals(ride.getStatus())){
                // Cancel and live button
                mBinding.button1.setVisibility(View.VISIBLE);
                mBinding.button1.setOnClickListener(mCancelListener);
                mBinding.button1.setText(R.string.button_cancel);
                mBinding.button2.setVisibility(View.VISIBLE);
                mBinding.button2.setOnClickListener(mLiveListener);
                mBinding.button2.setText(R.string.button_live);
            }else if(Ride.STATUS_COMPLETABLE.equals(ride.getStatus())){
                // Complete and live button
                mBinding.button1.setVisibility(View.VISIBLE);
                mBinding.button1.setOnClickListener(mCompleteListener);
                mBinding.button1.setText(R.string.button_complete);
                mBinding.button2.setVisibility(View.VISIBLE);
                mBinding.button2.setOnClickListener(mLiveListener);
                mBinding.button2.setText(R.string.button_live);
            }else if(Ride.STATUS_PING.equals(ride.getStatus())){
                // Cancel and Start button
                mBinding.button1.setVisibility(View.VISIBLE);
                mBinding.button1.setOnClickListener(mCancelListener);
                mBinding.button1.setText(R.string.button_cancel_ride);
                mBinding.button2.setVisibility(View.VISIBLE);
                mBinding.button2.setOnClickListener(mActiveListener);
                mBinding.button2.setText(R.string.button_start_ride);
            }else if(Ride.STATUS_ACTIVE.equals(ride.getStatus())){
                // Refresh and Cancel button
                if(ride.getDirection()!=null){
                    mBinding.button1.setVisibility(View.VISIBLE);
                    mBinding.button1.setOnClickListener(mCancelListener);
                    mBinding.button1.setText(R.string.button_cancel_ride);
                    mBinding.button2.setVisibility(View.VISIBLE);
                    mBinding.button2.setOnClickListener(mLiveListener);
                    mBinding.button2.setText(R.string.button_live);
                }else{
                    mBinding.button1.setVisibility(View.VISIBLE);
                    mBinding.button1.setOnClickListener(mActualiseListener);
                    mBinding.button1.setText(R.string.button_actualize);
                    mBinding.button2.setVisibility(View.VISIBLE);
                    mBinding.button2.setOnClickListener(mCancelListener);
                    mBinding.button2.setText(R.string.button_cancel_ride);
                }
            }else{
                // Refresh and Cancel button
                mBinding.button1.setVisibility(View.GONE);
                mBinding.button1.setOnClickListener(null);
                mBinding.button2.setVisibility(View.GONE);
                mBinding.button2.setOnClickListener(null);
            }
        }

        // Ride points
        List<RidePoint> points = rideWithDatas.getRidepoints();
        mAdapter.setItems(points);
    }

    private void setupUI() {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

        mBinding.setRideId(String.valueOf(rideId));

        NavController navController = NavHostFragment.findNavController(this);
        mBinding.toolbar.setNavigationOnClickListener(
                v -> {
                    navController.popBackStack();
                });
        mActualiseListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(token!=null && rideId!=null) {
                    progressDialog.show();
                    ridesViewModel.direction(token, rideId);
                }
            }
        };
        mActiveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(token!=null && rideId!=null) {
                    progressDialog.show();
                    ridesViewModel.active(token, rideId);
                }
            }
        };
        mCancelListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(token!=null && rideId!=null) {
                    progressDialog.show();
                    ridesViewModel.cancel(token, rideId);
                }
            }
        };
        mCompleteListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(token!=null && rideId!=null) {
                    progressDialog.show();
                    ridesViewModel.complete(token, rideId);
                }
            }
        };
        mLiveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rideId!=null) {
                    RidePointsFragmentDirections.ActionRidePointsFragmentToRideMapFragment action = RidePointsFragmentDirections
                            .actionRidePointsFragmentToRideMapFragment(rideId);
                    NavHostFragment.findNavController(RidePointsFragment.this).navigate(action);
                }
            }
        };

        mBinding.loaderErrorView.getButton().setOnClickListener(
                v -> {
                    loadRide();
                    mBinding.setIsLoading(true);
                    mBinding.setShowError(false);
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
                final String[] PERMISSIONS = {Manifest.permission.CALL_PHONE};
                //Asking request Permissions
                ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, 9);
            }
        }
    }

    private void phoneCall(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    private OnClickItemListener<RidePoint> mListener = (v, pos, item) -> {
        if(item==null){
            return;
        }
        RidePoint ridePoint = item;
        switch (v.getId()){
            case R.id.button_call:
                if (item.getUser() != null && item.getUser().getPhone() != null) {
                    onCallBtnClick(item.getUser().getPhone());
                }
            break;
            case R.id.button_cancel:
                if(ridePoint!=null) {
                    if(!RidePoint.STATUS_CANCELED.equals(ridePoint.getStatus())) {
                        progressDialog.show();
                        rideViewModel.cancelRidePoint(token, ridePoint.getRid());
                    }
                }
            break;
            default:
                if(ridePoint!=null) {
                    RidePointsFragmentDirections.ActionRidePointsFragmentToRidePointFragment action = RidePointsFragmentDirections
                            .actionRidePointsFragmentToRidePointFragment(ridePoint.getRid());
                    NavHostFragment.findNavController(RidePointsFragment.this).navigate(action);
                }
            break;
        }
    };

    public static Uri getUri(Long rideId){
        return Uri.parse("http://navetteclub.com/ride/" + rideId );
    }

}

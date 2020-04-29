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
import com.navetteclub.database.entity.ItemWithDatas;
import com.navetteclub.database.entity.Ride;
import com.navetteclub.database.entity.RidePoint;
import com.navetteclub.database.entity.RidePointWithDatas;
import com.navetteclub.database.entity.RideWithDatas;
import com.navetteclub.database.entity.User;
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

    private RideWithDatas rideWithDatas;

    private RideViewModel rideViewModel;

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
                    progressDialog.hide();

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
                    progressDialog.hide();

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
                    progressDialog.hide();

                    if(result.getError()!=null){
                        showSweetError(getString(result.getError()));
                    }

                    if(result.getSuccess()!=null){
                        this.setRide(result.getSuccess());
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setContentText("Votre course a bien annulé!")
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

    private void setRide(RideWithDatas rideWithDatas) {
        mBinding.liveButton.setVisibility(View.GONE);
        this.rideWithDatas = rideWithDatas;
        if(rideWithDatas==null) return;

        // Ride
        Ride ride = rideWithDatas.getRide();
        if(ride!=null){
            if(Ride.STATUS_COMPLETABLE.equals(ride.getStatus())){
                mBinding.actionButton.setText(R.string.button_complete);
                mBinding.actionButton.setVisibility(View.VISIBLE);
                mBinding.liveButton.setVisibility(View.VISIBLE); // <-- here
                mBinding.actualizeButton.setVisibility(View.GONE);
            }else if(Ride.STATUS_PING.equals(ride.getStatus())){
                mBinding.actionButton.setText(R.string.button_start_ride);
                mBinding.actionButton.setVisibility(View.VISIBLE);
                mBinding.liveButton.setVisibility(View.GONE);
                mBinding.actualizeButton.setVisibility(View.GONE);
            }else if(Ride.STATUS_ACTIVE.equals(ride.getStatus())){
                mBinding.actionButton.setText(R.string.button_cancel_ride);
                mBinding.actionButton.setVisibility(View.VISIBLE);
                mBinding.liveButton.setVisibility(View.VISIBLE);
                if(ride.getDirection()!=null){
                    mBinding.actualizeButton.setVisibility(View.GONE);
                }else{
                    mBinding.actualizeButton.setVisibility(View.VISIBLE);
                }
            }else{
                mBinding.actionButton.setVisibility(View.GONE);
                mBinding.liveButton.setVisibility(View.GONE);
                mBinding.actualizeButton.setVisibility(View.GONE);
            }
        }

        // Ride points
        List<RidePointWithDatas> points = rideWithDatas.getPoints();
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
        mBinding.actualizeButton.setOnClickListener(
                v -> {
                    if(token!=null && rideId!=null) {
                        progressDialog.show();
                        ridesViewModel.direction(token, rideId);
                    }
                });
        mBinding.liveButton.setOnClickListener(
                v -> {
                    if(rideWithDatas!=null) {
                        RidePointsFragmentDirections.ActionRidePointFragmentToRideMapFragment action = RidePointsFragmentDirections
                                .actionRidePointFragmentToRideMapFragment(
                                        rideWithDatas.getRide().getId());
                        NavHostFragment.findNavController(RidePointsFragment.this).navigate(action);
                    }
                });
        mBinding.actionButton.setOnClickListener(v->{
            if(token!=null && rideId!=null){
                if(rideWithDatas==null) return;
                Ride ride = rideWithDatas.getRide();
                if(ride!=null){
                    if(Ride.STATUS_COMPLETABLE.equals(ride.getStatus())){
                        progressDialog.show();
                        ridesViewModel.complete(token, rideId);
                    }
                    if(Ride.STATUS_PING.equals(ride.getStatus())){
                        progressDialog.show();
                        ridesViewModel.start(token, rideId);
                    }else if(Ride.STATUS_ACTIVE.equals(ride.getStatus())){
                        progressDialog.show();
                        ridesViewModel.cancel(token, rideId);
                    }
                }
            }
        });

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

    private OnClickItemListener<RidePointWithDatas> mListener = (v, pos, item) -> {
        if(item==null){
            return;
        }
        switch (v.getId()){
            case R.id.button_call:
                if (item.getUser() != null && item.getUser().getPhone() != null) {
                    onCallBtnClick(item.getUser().getPhone());
                }
            break;
            case R.id.button_cancel:
                RidePoint ridePoint = item.getRidePoint();
                if(ridePoint!=null) {
                    if(!RidePoint.STATUS_CANCELED.equals(ridePoint.getStatus())) {
                        progressDialog.show();
                        rideViewModel.cancelRidePoint(token, ridePoint.getRid());
                    }
                }
            break;
        }
    };

    public static Uri getUri(Long rideId){
        return Uri.parse("http://navetteclub.com/ride/" + rideId );
    }

}

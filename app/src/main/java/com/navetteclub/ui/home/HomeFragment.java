package com.navetteclub.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.navetteclub.R;
import com.navetteclub.database.entity.ClubAndPoint;
import com.navetteclub.database.entity.Point;
import com.navetteclub.databinding.FragmentHomeBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.ui.order.OrderFragmentDirections;
import com.navetteclub.ui.order.SearchType;
import com.navetteclub.vm.ClubViewModel;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.Utils;
import com.navetteclub.vm.GoogleViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback, OnClickItemListener<ClubAndPoint> {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private GoogleMap mMap;

    private FragmentHomeBinding mBinding;

    private Location mLastKnownLocation;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private ClubRecyclerViewAdapter mAdapter;

    private List<ClubAndPoint> mClubs;

    private ClubViewModel clubViewModel;

    private OrderViewModel orderViewModel;

    private GoogleViewModel googleViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        mAdapter = new ClubRecyclerViewAdapter(this);
        RecyclerView recyclerView = mBinding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupMap();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        setupClubViewModel(factory);
        setupOrderViewModel(factory);
        setupGoogleViewModel(factory);
        setupUi();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
        // Show club in map
        updateClubUI(mClubs);
    }

    private void setupMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        if (Utils.requestingLocationUpdates(requireContext())) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }
    }

    private void setupUi() {
        mBinding.createOrderButton.setOnClickListener(v -> {
            orderViewModel.refresh();
            Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_navigation_order);
        });
        mBinding.errorLoader.getButton().setOnClickListener(v -> loadClubs());
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        loadClubs();
                    }
                });
    }

    private void setupOrderViewModel(MyViewModelFactory factory) {
        orderViewModel = new ViewModelProvider(requireActivity(), factory).get(OrderViewModel.class);
    }

    private void setupGoogleViewModel(MyViewModelFactory factory) {
        googleViewModel = new ViewModelProvider(requireActivity(), factory).get(GoogleViewModel.class);
    }

    private void setupClubViewModel(MyViewModelFactory factory) {
        clubViewModel = new ViewModelProvider(requireActivity(), factory).get(ClubViewModel.class);
        clubViewModel.getClubsResult().observe(getViewLifecycleOwner(),
                result -> {
                    if (result == null) {
                        return;
                    }
                    mBinding.setIsLoading(false);
                    if (result.getError() != null) {
                        mBinding.setIsErrorLoading(true);
                        Toast.makeText(requireContext(), result.getError(), Toast.LENGTH_SHORT).show();
                    }else{
                        mBinding.setIsErrorLoading(false);
                    }
                });
        clubViewModel.getClubs().observe(getViewLifecycleOwner(),
                clubAndPoints -> {
                    if (clubAndPoints == null) {
                        return;
                    }
                    mClubs = clubAndPoints;
                    updateClubUI(clubAndPoints);
                });
        loadClubs();
    }

    private void loadClubs() {
        mBinding.setIsLoading(true);
        mBinding.setIsErrorLoading(false);
        clubViewModel.load();
    }

    private void updateClubUI(List<ClubAndPoint> clubs) {
        if(clubs!=null){
            mAdapter.setItems(clubs);
        }
        if(mMap!=null && clubs !=null) {
            for(ClubAndPoint item: clubs){
                if(item.getClub()!=null && item.getPoint()!=null){
                    LatLng latLng = new LatLng(item.getPoint().getLat(), item.getPoint().getLng());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(item.getClub().getName()));
                }
            }
        }
    }

    private void getDeviceLocation() {
        try {
            if (checkPermissions()) {
                Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), task -> {
                    Log.d(TAG, "locationResult.addOnCompleteListener");
                    if (task.isSuccessful()) {
                        mLastKnownLocation = (Location) task.getResult();
                        if (mLastKnownLocation != null && mMap != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), 15));
                        }
                    } else {
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (checkPermissions()) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                requestPermissions();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    mBinding.getRoot(),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(requireActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onClick(View v, int position, ClubAndPoint item) {
        googleViewModel.refresh();
        orderViewModel.refresh();
        orderViewModel.setClubLiveData(item.getClub());
        orderViewModel.setClubPointLiveData(item.getPoint());
        Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_navigation_order);
    }
}

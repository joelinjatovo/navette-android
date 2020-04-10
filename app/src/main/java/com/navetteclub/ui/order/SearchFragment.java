package com.navetteclub.ui.order;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.navetteclub.R;
import com.navetteclub.databinding.FragmentSearchBinding;
import com.navetteclub.ui.MainActivity;
import com.navetteclub.ui.SplashActivity;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.UiUtils;
import com.navetteclub.utils.Utils;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = SearchFragment.class.getSimpleName();;

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private static final float MAP_ZOOM = 15;

    private SearchType searchType;

    private GoogleMap mMap;

    private Location mLastKnownLocation;

    private LatLng mLocation;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private FragmentSearchBinding mBinding;

    private Geocoder geocoder;

    private static Handler handler;

    private OrderViewModel orderViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            // The getSearchType() method will be created automatically.
            searchType = SearchFragmentArgs.fromBundle(getArguments()).getSearchType();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUi();

        setupViewModel();

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        if (autocompleteFragment != null) {
            Objects.requireNonNull(autocompleteFragment.getView()).setBackgroundColor(getResources().getColor(R.color.white));

            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
            autocompleteFragment.setPlaceFields(fields);

            // Set up a PlaceSelectionListener to handle the response.
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getLatLng());
                    moveCamera(place.getLatLng());
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.i(TAG, "An error occurred: " + status);
                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnCameraIdleListener(() -> {
            LatLng latLng = mMap.getCameraPosition().target;
            Log.i(TAG, "OnCameraIdleListener: " + latLng);

            if(latLng.latitude != 0.0 && latLng.longitude != 0.0){
                setLocation(latLng);
            }
        });

        mMap.setOnMapClickListener(this::moveCamera);

        geocoder = new Geocoder(requireContext());
    }

    private void setupViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());

        orderViewModel = new ViewModelProvider(this, factory).get(OrderViewModel.class);
        orderViewModel.getOrigin().observe(getViewLifecycleOwner(),
                originPoint -> {
                    Log.d(TAG, "ORIGIN observe()");
                    if(searchType == SearchType.ORIGIN) {
                        Log.d(TAG, "ORIGIN observe() OK");
                        if(originPoint == null){
                            getDeviceLocation();
                            Log.d(TAG, "ORIGIN getDeviceLocation()");
                            return;
                        }

                        LatLng latLng = new LatLng(originPoint.getLat(), originPoint.getLng());

                        if(latLng.latitude != 0.0 && latLng.longitude != 0.0){
                            getDeviceLocation();
                            Log.d(TAG, "ORIGIN getDeviceLocation() 2.0");
                        }else{
                            moveCamera(latLng);
                            Log.d(TAG, "ORIGIN moveCamera()");
                        }

                    }
                });

        orderViewModel.getRetours().observe(getViewLifecycleOwner(),
                retoursPoint -> {
                    Log.d(TAG, "RETOURS observe()");
                    if(searchType == SearchType.RETOURS) {
                        Log.d(TAG, "RETOURS observe() OK");
                        if(retoursPoint == null){
                            getDeviceLocation();
                            Log.d(TAG, "RETOURS getDeviceLocation()");
                            return;
                        }

                        LatLng latLng = new LatLng(retoursPoint.getLat(), retoursPoint.getLng());
                        if(latLng.latitude != 0.0 && latLng.longitude != 0.0){
                            getDeviceLocation();
                            Log.d(TAG, "RETOURS getDeviceLocation() 2.0");
                        }else{
                            moveCamera(latLng);
                            Log.d(TAG, "RETOURS moveCamera()");
                        }

                    }
                });
    }

    private void setupUi() {
        if(searchType == SearchType.ORIGIN){
            mBinding.title.setText(R.string.title_origin);
            mBinding.toolbar.setTitle(R.string.title_origin);
        }

        if(searchType == SearchType.RETOURS){
            mBinding.title.setText(R.string.title_retours);
            mBinding.toolbar.setTitle(R.string.title_retours);
        }

        mBinding.myLocation.setOnClickListener(
                v->{
                    getDeviceLocation();
                });

        mBinding.okButton.setOnClickListener(
                v -> {
                    if(searchType == SearchType.ORIGIN){
                        Log.d(TAG, "ORIGIN");
                        orderViewModel.setOrigin(mBinding.locationTitle.getText().toString(), mLocation, true);
                    }
                    if(searchType == SearchType.RETOURS){
                        Log.d(TAG, "RETOURS");
                        orderViewModel.setReturn(mBinding.locationTitle.getText().toString(), mLocation, true);
                    }
                    Navigation.findNavController(v).popBackStack();
                });
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Check that the user hasn't revoked permissions by going to Settings.
        if (Utils.requestingLocationUpdates(requireContext())) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    private void moveCamera(LatLng latLng) {
        if(mMap!=null){
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(MAP_ZOOM));
        }
    }

    private void setLocation(LatLng latLng) {
        mBinding.setIsLoading(true);
        mLocation = latLng;

        if(handler!=null){
            Log.e(TAG, "removeCallbacksAndMessages" );
            handler.removeCallbacksAndMessages(null);
        }else{
            handler = new Handler();
        }

        int secondsDelayed = 3;
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.e(TAG, "postDelayed" );
                getGeocode(latLng);
            }
        }, secondsDelayed * 1000);
    }

    private void getGeocode(LatLng latLng) {
        try {
            List<Address> addressList= geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (addressList != null && addressList.size() > 0) {
                String locality = addressList.get(0).getAddressLine(0);
                String country = addressList.get(0).getCountryName();
                Log.e(TAG, "locality = " + locality);
                Log.e(TAG, "country = " + country);
                if (!locality.isEmpty() && !country.isEmpty()){
                    mBinding.locationTitle.setText(locality);
                    mBinding.locationSubtitle.setText(country);
                    mBinding.setIsLoading(false);
                }else{
                    mBinding.setIsLoading(false);
                }
            }else{
                mBinding.setIsLoading(false);
            }
        } catch (IOException ex) {
            Log.e(TAG, "Exception: %s", ex);
            mBinding.setIsLoading(false);
        }

    }

    private void getDeviceLocation() {
        try {
            if (checkPermissions()) {
                mBinding.setIsLoading(true);
                Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(),
                        task -> {
                            Log.d(TAG, "locationResult.addOnCompleteListener");
                            if (task.isSuccessful()) {
                                mLastKnownLocation = (Location) task.getResult();
                                if (mLastKnownLocation != null) {
                                    LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                    moveCamera(latLng);
                                }
                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    mBinding.getRoot(),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(requireActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}

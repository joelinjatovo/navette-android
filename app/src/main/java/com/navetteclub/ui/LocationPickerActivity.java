package com.navetteclub.ui;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.navetteclub.R;
import com.navetteclub.databinding.ActivityLocationPickerBinding;
import com.navetteclub.utils.Constants;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.Utils;

import java.io.IOException;
import java.util.List;

public class LocationPickerActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = LocationPickerActivity.class.getSimpleName();;

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    public static final String INTENT_EXTRA_NAME = "location_name";
    public static final String INTENT_EXTRA_LAT = "location_lat";
    public static final String INTENT_EXTRA_LNG = "location_lng";

    private static final float MAP_ZOOM = 15;

    private GoogleMap mMap;

    private Location mLastKnownLocation;

    private String mName;

    private LatLng mLocation;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private ActivityLocationPickerBinding mBinding;

    private Geocoder geocoder;

    private static Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityLocationPickerBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        //if you want to open the location on the LocationPickerActivity through intent
        Intent i = getIntent();
        if (i != null) {
            Bundle extras = i.getExtras();
            if (extras != null) {
                mName = extras.getString(INTENT_EXTRA_NAME);
                mLocation = new LatLng(
                        extras.getDouble(INTENT_EXTRA_LAT, Constants.DEFAULT_LOCATION.latitude),
                        extras.getDouble(INTENT_EXTRA_LNG, Constants.DEFAULT_LOCATION.longitude)
                );
            }
        }

        setupUi();
        setupMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(mLocation!=null){
            moveCamera(mLocation);
        }else{
            moveCamera(Constants.DEFAULT_LOCATION);
        }
        mMap.setOnCameraIdleListener(() -> {
            LatLng latLng = mMap.getCameraPosition().target;
            if(latLng.latitude != 0.0 && latLng.longitude != 0.0){
                setLocation(latLng);
            }
        });
        mMap.setOnMapClickListener(this::moveCamera);
        geocoder = new Geocoder(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INTENT_EXTRA_NAME, mName);
        outState.putDouble(INTENT_EXTRA_LAT, mLocation.latitude);
        outState.putDouble(INTENT_EXTRA_LNG, mLocation.longitude);
    }

    private void setupUi() {
        mBinding.myLocation.setOnClickListener(v -> getDeviceLocation());
        mBinding.okButton.setOnClickListener(
                v -> {
                    String name = mBinding.locationTitle.getText().toString();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(INTENT_EXTRA_LAT, mLocation.latitude);
                    returnIntent.putExtra(INTENT_EXTRA_LNG, mLocation.longitude);
                    returnIntent.putExtra(INTENT_EXTRA_NAME, name);
                    setResult(RESULT_OK,returnIntent);
                    finish();
                });
    }

    private void setupMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Check that the user hasn't revoked permissions by going to Settings.
        if (Utils.requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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
            handler.removeCallbacksAndMessages(null);
        }else{
            handler = new Handler();
        }

        int secondsDelayed = 3;
        handler.postDelayed(new Runnable() {
            public void run() {
                getGeocode(latLng);
            }
        }, secondsDelayed * 1000);
    }

    private void getGeocode(LatLng latLng) {
        try {
            List<Address> addressList= geocoder.getFromLocation(latLng.latitude, latLng.longitude,1);
            if (addressList != null && addressList.size() > 0) {
                String locality = addressList.get(0).getAddressLine(0);
                String country = addressList.get(0).getCountryName();
                if (locality!=null && !locality.isEmpty() && country!=null && !country.isEmpty()){
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
                locationResult.addOnCompleteListener(this,
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
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    mBinding.getRoot(),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(LocationPickerActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}

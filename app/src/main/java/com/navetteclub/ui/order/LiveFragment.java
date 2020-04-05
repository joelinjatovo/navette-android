package com.navetteclub.ui.order;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.navetteclub.BuildConfig;
import com.navetteclub.R;
import com.navetteclub.api.models.google.GoogleDirectionResponse;
import com.navetteclub.api.models.google.Leg;
import com.navetteclub.api.models.google.Route;
import com.navetteclub.api.services.GoogleApiService;
import com.navetteclub.database.entity.Point;
import com.navetteclub.databinding.FragmentLiveBinding;
import com.navetteclub.databinding.FragmentOrderMapBinding;
import com.navetteclub.services.LocationUpdatesService;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.Utils;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LiveFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = LiveFragment.class.getSimpleName();

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;


    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    private GoogleMap mMap;

    private Location mLastKnownLocation;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private FragmentLiveBinding mBinding;

    private OrderViewModel orderViewModel;

    private Polyline line;

    private LatLng mOldOrigin;

    private LatLng mOrigin;

    private LatLng mOldDestination;

    private LatLng mDestination;

    private LatLng mRetours;

    private Marker mOriginMarker;

    private Marker mDestinationMarker;

    private Marker mRetoursMarker;

    private Marker myPositionMarker;

    private BottomSheetBehavior sheetBehavior;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onCreateView");

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_live, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        setupMap();

        myReceiver = new MyReceiver();

        // Check that the user hasn't revoked permissions by going to Settings.
        if (Utils.requestingLocationUpdates(requireContext())) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG + "Cycle", "onViewCreated");

        setupBottomSheet();

        setupUi();

        setupOrderViewModel();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        requireActivity().bindService(new Intent(requireContext(), LocationUpdatesService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(myReceiver, new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(myReceiver);
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            requireActivity().unbindService(mServiceConnection);
            mBound = false;
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG + "Cycle", "onDestroyView");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mService.requestLocationUpdates();
            } else {
                // Permission denied.
                Snackbar.make(
                        mBinding.getRoot(),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
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

    private void setupBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from(mBinding.bottomSheets.bottomSheet);
    }

    private void setupOrderViewModel() {
        orderViewModel = new ViewModelProvider(this, new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);

        orderViewModel.getOrderLiveData().observe(getViewLifecycleOwner(),
                orderWithDatas -> {
                    if(orderWithDatas == null){
                        return;
                    }

                    // Order
                    if(orderWithDatas.getOrder() != null){
                        mBinding.setDistance(orderWithDatas.getOrder().getDistance());
                        mBinding.setDelay(orderWithDatas.getOrder().getDelay());

                        String encodedString = orderWithDatas.getOrder().getDirection();
                        if(encodedString!=null && mMap!=null){
                            //Remove previous line from map
                            if (line != null) {
                                line.remove();
                            }

                            List<LatLng> list = Utils.decodePoly(encodedString);
                            line = mMap.addPolyline(new PolylineOptions()
                                    .addAll(list)
                                    .width(5)
                                    .color(R.color.colorAccent)
                                    .geodesic(true)
                            );
                        }
                    }

                    // Points
                    if(orderWithDatas.getPoints()!=null){
                        Log.d(TAG, "Size=" + orderWithDatas.getPoints().size());
                        // Origin
                        if(orderWithDatas.getPoints().size()>0){
                            Point point = orderWithDatas.getPoints().get(0);
                            if(point!=null){
                                mBinding.setOrigin(point);

                                LatLng origin = new LatLng(
                                        point.getLat(),
                                        point.getLng()
                                );

                                mOrigin = origin;

                                drawMarker(origin, 0);

                                // Zoom map
                                if(mMap!=null){
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));
                                }
                            }
                        }

                        // Destination
                        if(orderWithDatas.getPoints().size()>1) {
                            Point point = orderWithDatas.getPoints().get(1);
                            if(point!=null) {

                                mBinding.setDestination(point);

                                LatLng destination = new LatLng(
                                        point.getLat(),
                                        point.getLng()
                                );

                                mDestination = destination;

                                drawMarker(destination, 1);
                            }
                        }

                        // Retours
                        if(orderWithDatas.getPoints().size()>2) {
                            Point point = orderWithDatas.getPoints().get(2);
                            if(point!=null) {

                                mBinding.setRetours(point);

                                LatLng retours = new LatLng(
                                        point.getLat(),
                                        point.getLng()
                                );

                                mRetours = retours;

                                drawMarker(retours, 2);
                            }
                        }

                        // Distance & Delay
                        getDirectionAndDistance("driving");
                    }
                });

    }

    private void drawMarker(LatLng point, int index) {
        if(mMap==null){
            return;
        }

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(point);

        Marker marker = null;
        switch (index){
            case 0: // Origin
                marker = mOriginMarker;
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            break;
            case 1: // Destination
                marker = mDestinationMarker;
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            break;
            case 2: // Retours
                marker = mRetoursMarker;
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            break;
        }

        if(marker!=null){
            // remove old marker
            marker.remove();
        }

        // Add new marker to the Google Map Android API V2
        marker = mMap.addMarker(options);

        // Set marker
        switch (index){
            case 0: // Origin
                mOriginMarker = marker;
                break;
            case 1: // Destination
                mDestinationMarker = marker;
                break;
            case 2: // Retours
                mRetoursMarker = marker;
                break;
        }
    }

    private void setupUi() {
        // Nothing
        mBinding.switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    // Live update location
                    Toast.makeText(requireContext(), "requestLocationUpdatesButton.click",
                            Toast.LENGTH_SHORT).show();
                    if (!checkPermissions()) {
                        requestPermissions();
                    } else {
                        mService.requestLocationUpdates();
                    }
                }else{
                    // Remove live location update
                    Toast.makeText(requireContext(), "removeLocationUpdatesButton.click",
                            Toast.LENGTH_SHORT).show();
                    mService.removeLocationUpdates();
                }
            }
        });
    }

    private void expandOrderDetails() {
        if(sheetBehavior != null && mOrigin != null && mDestination != null){
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void getDeviceLocation() {
        try {
            if (checkPermissions()) {
                Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Log.d(TAG, "locationResult.addOnCompleteListener");
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();

                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), 10));

                                LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                orderViewModel.setOrigin(getString(R.string.my_location), latLng, true);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            }
                        }
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
                            // Request permission
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

    private void getDirectionAndDistance(String type) {
        if(mOrigin == null ){
            return;
        }

        if(mDestination == null ){
            return;
        }

        if(mOldOrigin != null
                && mOrigin.latitude == mOldOrigin.latitude
                && mOrigin.longitude == mOldOrigin.longitude
                && mOldDestination != null
                && mDestination.latitude == mOldDestination.latitude
                && mDestination.longitude == mOldDestination.longitude){
            return;
        }

        mOldOrigin = mOrigin;

        mOldDestination = mDestination;

        String url = "https://maps.googleapis.com/maps/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String key = getString(R.string.google_maps_key);

        String waypoints = "optimize:true|"
                + mDestination.latitude + "," + mDestination.longitude  // Ravimpotsy
                + "|" + mOrigin.latitude + "," + mOrigin.longitude
                + "|" + "-18.9153215,47.5385984" // Shoprite
                + "|" + "-18.9127615,47.5344172" // Jet club
                + "|" + mDestination.latitude + "," + mDestination.longitude; // Ravimpotsy


        GoogleApiService service = retrofit.create(GoogleApiService.class);
        Call<GoogleDirectionResponse> call = service.getDirection(
                key,
                "metric",
                mOrigin.latitude + "," + mOrigin.longitude,
                mDestination.latitude + "," + mDestination.longitude,
                type,
                waypoints
        );

        // Show loader
        expandOrderDetails();
        mBinding.setIsLoadingDirection(true);
        mBinding.setShowErrorLoader(false);

        call.enqueue(new Callback<GoogleDirectionResponse>() {
            @Override
            public void onResponse(@NonNull Call<GoogleDirectionResponse> call, @NonNull Response<GoogleDirectionResponse> response) {
                Log.d(TAG + "Map", response.toString());
                // Hide loader
                mBinding.setIsLoadingDirection(false);

                try {
                    // This loop will go through all the results and add marker on each location.
                    GoogleDirectionResponse googleDirectionResponse = response.body();

                    Log.e(TAG, "googleDirectionResponse = " + googleDirectionResponse);
                    for (int i = 0; i < googleDirectionResponse.getRoutes().size(); i++) {
                        Route route = googleDirectionResponse.getRoutes().get(i);
                        for(Leg leg: route.getLegs()){
                            String distance = leg.getDistance().getText();
                            orderViewModel.setDistance(distance);

                            String time = leg.getDuration().getText();
                            orderViewModel.setDelay(time);

                            Log.d(TAG, String.format("Distance:%s, Duration:%s", distance, time));
                        }

                        String encodedString = route.getOverviewPolyline().getPoints();
                        orderViewModel.setDirection(encodedString);
                    }

                    mBinding.setShowErrorLoader(false);
                } catch (Exception e) {
                    Log.e(TAG, "There is an error", e);
                    mBinding.setShowErrorLoader(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GoogleDirectionResponse> call, @NonNull Throwable t) {
                Log.e(TAG, t.toString(), t);

                mBinding.setIsLoadingDirection(false);
                mBinding.setShowErrorLoader(true);
            }
        });

    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                Toast.makeText(requireContext(), Utils.getLocationText(location),
                        Toast.LENGTH_SHORT).show();

                if(myPositionMarker!=null){
                    myPositionMarker.remove();
                }

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                myPositionMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in my location"));

                // Set the map's camera position to the current location of the device.
                if(mLastKnownLocation == null) {
                    mLastKnownLocation = location;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()), 10));
                }
            }
        }
    }

}

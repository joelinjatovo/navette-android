package com.navetteclub.ui.order;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import com.navetteclub.database.entity.Club;
import com.navetteclub.database.entity.Point;
import com.navetteclub.databinding.FragmentLiveBinding;
import com.navetteclub.databinding.FragmentOrderMapBinding;
import com.navetteclub.services.LocationUpdatesService;
import com.navetteclub.utils.Constants;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.UiUtils;
import com.navetteclub.utils.Utils;
import com.navetteclub.vm.GoogleViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
    private static final float MAP_ZOOM = 25;


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

    private GoogleViewModel googleViewModel;

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

        setupGoogleViewModel();
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

    private void setupGoogleViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());

        googleViewModel = new ViewModelProvider(requireActivity(),
                factory).get(GoogleViewModel.class);

        googleViewModel.getDirectionResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }

                    mBinding.setIsLoadingDirection(false);
                    mBinding.setShowErrorLoader(false);

                    if(result.body()!=null){
                        for (int i = 0; i < result.body().getRoutes().size(); i++) {
                            Route route = result.body().getRoutes().get(i);
                            for(Leg leg: route.getLegs()){
                                orderViewModel.setDistance(leg.getDistance().getText());
                                orderViewModel.setDelay(leg.getDuration().getText());
                            }
                            orderViewModel.setDirection(route.getOverviewPolyline().getPoints());
                        }
                    }
                });

        googleViewModel.getErrorResult().observe(getViewLifecycleOwner(),
                error -> {
                    if(error==null){
                        return;
                    }

                    mBinding.setIsLoadingDirection(false);

                    showDirectionError(error);

                });

    }

    private void showDirectionError(String error) {
        mBinding.setShowErrorLoader(false);
    }

    private void setupOrderViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());

        orderViewModel = new ViewModelProvider(this, factory).get(OrderViewModel.class);

        orderViewModel.getOrigin().observe(getViewLifecycleOwner(),
                originPoint -> {
                    mBinding.setOrigin(originPoint);
                    mOrigin = new LatLng(originPoint.getLat(), originPoint.getLng());
                    drawOriginMarker(originPoint);
                    if(mMap!=null){
                        // Zoom map
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, MAP_ZOOM));
                    }
                    loadDirection();
                });

        orderViewModel.getDestination().observe(getViewLifecycleOwner(),
                destinationPoint -> {
                    mBinding.setDestination(destinationPoint);
                    mDestination = new LatLng(destinationPoint.getLat(), destinationPoint.getLng());
                    drawDestinationMarker(destinationPoint, orderViewModel.getOrder().getClub());
                    if(mMap!=null){
                        // Zoom map
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDestination, MAP_ZOOM));
                    }
                    loadDirection();
                });

        orderViewModel.getRetours().observe(getViewLifecycleOwner(),
                retoursPoint -> {
                    mBinding.setRetours(retoursPoint);
                    mRetours = new LatLng(retoursPoint.getLat(), retoursPoint.getLng());
                    drawRetoursMarker(retoursPoint);
                    if(mMap!=null){
                        // Zoom map
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mRetours, MAP_ZOOM));
                    }
                });

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
                });

    }

    private void loadDirection() {
        if(mOrigin!=null && mDestination!=null){
            mBinding.setIsLoading(true);
            mBinding.setShowErrorLoader(false);
            expandOrderDetails();
            googleViewModel.loadDirection(getString(R.string.google_maps_key), mOrigin, mDestination);
        }
    }

    private void drawOriginMarker(Point point) {
        if(mMap==null){
            return;
        }

        if(mOriginMarker!=null){
            mOriginMarker.remove();
        }

        LatLng latLng = new LatLng(point.getLat(),point.getLng());
        MarkerOptions options = new MarkerOptions(); // Creating MarkerOptions
        options.position(latLng); // Setting the position of the marker
        options.icon(UiUtils.getBitmapFromMarkerView(requireContext(), point.getName()));
        //options.anchor(1, 0.5f);

        mOriginMarker = mMap.addMarker(options);
    }

    private void drawRetoursMarker(Point point) {
        if(mMap==null){
            return;
        }

        if(mRetoursMarker!=null){
            mRetoursMarker.remove();
        }

        LatLng latLng = new LatLng(point.getLat(),point.getLng());
        MarkerOptions options = new MarkerOptions(); // Creating MarkerOptions
        options.position(latLng); // Setting the position of the marker
        options.icon(UiUtils.getBitmapFromMarkerView(requireContext(), point.getName()));
        //options.anchor(1, 0.5f);

        mRetoursMarker = mMap.addMarker(options);
    }

    private void drawDestinationMarker(Point point, Club club) {
        if(mMap==null){
            return;
        }

        if(mDestinationMarker!=null){
            mDestinationMarker.remove();
        }

        LatLng latLng = new LatLng(point.getLat(),point.getLng());
        MarkerOptions options = new MarkerOptions(); // Creating MarkerOptions
        options.position(latLng); // Setting the position of the marker
        if(club!=null){
            new Picasso.Builder(requireContext())
                    .build()
                    .load(Constants.getBaseUrl() + club.getImageUrl())
                    .resize(64,64)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            // loaded bitmap is here (bitmap)
                            options.icon(UiUtils.getBitmapFromMarkerView(requireContext(), club.getName(), bitmap));
                            //options.anchor(0.5f, 1);
                            mDestinationMarker = mMap.addMarker(options);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            mDestinationMarker = mMap.addMarker(options);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });
        }else{
            options.icon(UiUtils.getBitmapFromMarkerView(requireContext(), point.getName()));
            mDestinationMarker = mMap.addMarker(options);
        }
    }

    private void setupUi() {
        // Nothing
        mBinding.switchView.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
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
                locationResult.addOnCompleteListener(requireActivity(),
                        task -> {
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

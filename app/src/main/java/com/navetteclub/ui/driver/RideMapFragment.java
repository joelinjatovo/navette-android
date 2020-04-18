package com.navetteclub.ui.driver;

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
import androidx.annotation.StringRes;
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
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.google.Leg;
import com.navetteclub.api.models.google.Route;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.UserApiService;
import com.navetteclub.database.entity.Club;
import com.navetteclub.database.entity.ClubAndPoint;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.entity.RidePointWithDatas;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentOrderMapBinding;
import com.navetteclub.databinding.FragmentRideMapBinding;
import com.navetteclub.services.LocationUpdatesService;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.Utils;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.GoogleViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;
import com.navetteclub.vm.RidesViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = RideMapFragment.class.getSimpleName();

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

    private FragmentRideMapBinding mBinding;

    private Marker myPositionMarker;

    private MyLocationReceiver myLocationReceiver;

    private AuthViewModel authViewModel;

    private GoogleViewModel googleViewModel;

    private RidesViewModel ridesViewModel;

    List<LatLng> list;

    Polyline line1;

    Polyline line2;

    private ArrayList<Point> mPoints;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onCreateView");
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ride_map, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        setupMap();

        myReceiver = new MyReceiver();
        myLocationReceiver = new MyLocationReceiver();

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
        setupUi();
    }

    private void setupRidesViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        ridesViewModel = new ViewModelProvider(requireActivity(), factory).get(RidesViewModel.class);
        ridesViewModel.getPointsLiveData().observe(getViewLifecycleOwner(),
                points -> {
                    if(points==null){
                        return;
                    }
                    if(points.getError()!=null){
                        if(points.getError() == R.string.error_401) { // Error 401: Unauthorized
                            authViewModel.logout(requireContext());
                        }else{
                            // Error loading
                            mBinding.setIsLoading(false);
                        }
                    }
                    if(points.getSuccess()!=null){
                        mBinding.setIsLoading(false);
                        ArrayList<RidePointWithDatas> items = (ArrayList<RidePointWithDatas>) points.getSuccess();
                        if(!items.isEmpty()){
                            mPoints = new ArrayList<>();
                            Point point = null;
                            for(RidePointWithDatas item: items){
                                if(item.getPoint()!=null) {
                                    mPoints.add(item.getPoint());
                                }
                            }

                            if(mPoints.size()>0){
                                loadDirection(mPoints.get(0), mPoints);
                            }
                        }
                    }
                });
    }

    private void setupAuthViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());

        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);
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
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(myReceiver, new IntentFilter(LocationUpdatesService.ACTION_BROADCAST_PUSHER));
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(myLocationReceiver, new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(myReceiver);
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(myLocationReceiver);
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
        setupAuthViewModel();
        setupGoogleViewModel();
        setupRidesViewModel();
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


    private void setupGoogleViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        googleViewModel = new ViewModelProvider(requireActivity(),
                factory).get(GoogleViewModel.class);
        googleViewModel.getDirection1Result().observe(getViewLifecycleOwner(),
                result -> {
                    if (result == null) {
                        return;
                    }
                    if(result.body()!=null){
                        for (int i = 0; i < result.body().getRoutes().size(); i++) {
                            Route route = result.body().getRoutes().get(i);
                            String encodedString = route.getOverviewPolyline().getPoints();
                            if(encodedString!=null){
                                drawLine(encodedString);
                            }
                            Log.d(TAG, "getWaypointOrder " + Arrays.toString(route.getWaypointOrder()));
                        }
                    }
                });

    }

    /**
     * *
     * Draw line from location update
     * @param latLng
     */
    private void drawLine(LatLng latLng) {
        if (mMap == null) {
            return;
        }

        if(list==null){
            list = new ArrayList<>();
        }

        list.add(latLng);

        //Remove previous line from map
        if (line2 != null) {
            line2.remove();
        }

        line2 = mMap.addPolyline(new PolylineOptions()
                .addAll(list)
                .width(10)
                .color(R.color.black)
                .geodesic(true)
        );
    }

    /**
     * *
     * Draw line from Google API
     * @param encodedString
     */
    private void drawLine(String encodedString) {
        if(mMap==null){
            return;
        }

        //Remove previous line from map
        if (line1 != null) {
            line1.remove();
        }

        List<LatLng> list = Utils.decodePoly(encodedString);
        line1 = mMap.addPolyline(new PolylineOptions()
                .addAll(list)
                .width(5)
                .color(R.color.colorAlert)
                .geodesic(true)
        );
    }

    private void loadDirection(Point clubPoint, List<Point> points) {
        LatLng origin = new LatLng(clubPoint.getLat(), clubPoint.getLng());
        LatLng destination = new LatLng(clubPoint.getLat(), clubPoint.getLng());
        String waypoints = "optimize:true";
        for(Point userPoint: points){
            waypoints += "|" + userPoint.getLat() + "," + userPoint.getLng();
        }
        googleViewModel.loadDirection(getString(R.string.google_maps_key), origin, destination, waypoints, true);

        if(mMap!=null) {
            LatLng latLng = new LatLng(clubPoint.getLat(), clubPoint.getLng());
            mMap.addMarker(new MarkerOptions().position(latLng).title(clubPoint.getName()));
            for(Point userPoint: points){
                latLng = new LatLng(userPoint.getLat(), userPoint.getLng());
                mMap.addMarker(new MarkerOptions().position(latLng).title(userPoint.getName()));
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

    private void sendLocationToServer(User user , Location location) {
        UserApiService service = RetrofitClient.getInstance().create(UserApiService.class);
        Call<RetrofitResponse<User>> call = service.addPosition(user.getAuthorizationToken(),
                new com.navetteclub.api.models.Location(location));
        call.enqueue(new Callback<RetrofitResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<User>> call,
                                   @NonNull Response<RetrofitResponse<User>> response) {
                Log.d(TAG, response.toString());
                RetrofitResponse<User> data = response.body();
                Log.d(TAG, response.code() + "  " + response.message());
                if(null != data){
                    Log.d(TAG, data.toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<User>> call,
                                  @NonNull Throwable throwable) {
                Log.w(TAG, throwable);
            }
        });
    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LatLng latLng = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (latLng != null) {
                Toast.makeText(requireContext(), latLng.toString(),
                        Toast.LENGTH_SHORT).show();

                // Draw car line
                drawLine(latLng);

                if(myPositionMarker!=null){
                    myPositionMarker.remove();
                }

                myPositionMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in my location"));

                // Set the map's camera position to the current location of the device.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
            }
        }
    }

    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyLocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                Toast.makeText(requireContext(), "Ito zao: " + Utils.getLocationText(location),
                        Toast.LENGTH_SHORT).show();

                User user = authViewModel.getUser();
                if(user!=null){
                    sendLocationToServer(user, location);
                }
            }
        }
    }

}

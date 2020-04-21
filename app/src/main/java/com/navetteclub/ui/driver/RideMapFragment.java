package com.navetteclub.ui.driver;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.os.Build;
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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

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
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.google.Leg;
import com.navetteclub.api.models.google.Route;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.UserApiService;
import com.navetteclub.database.entity.Club;
import com.navetteclub.database.entity.ClubAndPoint;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.entity.Ride;
import com.navetteclub.database.entity.RidePoint;
import com.navetteclub.database.entity.RidePointWithDatas;
import com.navetteclub.database.entity.RideWithDatas;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentOrderMapBinding;
import com.navetteclub.databinding.FragmentRideMapBinding;
import com.navetteclub.services.LocationUpdatesService;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.utils.Constants;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.MapUiUtils;
import com.navetteclub.utils.UiUtils;
import com.navetteclub.utils.Utils;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.GoogleViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;
import com.navetteclub.vm.RideViewModel;
import com.navetteclub.vm.RidesViewModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideMapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = RideMapFragment.class.getSimpleName();

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

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

    private RideViewModel rideViewModel;

    List<LatLng> list;

    Polyline line1;

    Polyline line2;

    private Marker mClubMarker;

    private ArrayList<Marker> mMarkers;

    private ArrayList<Point> mPoints;

    private String token;

    private Long rideId;

    private RideWithDatas rideWithDatas;

    private RidePointMapRecyclerViewAdapter mAdapter;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            token = RideMapFragmentArgs.fromBundle(getArguments()).getToken();
            rideId = RideMapFragmentArgs.fromBundle(getArguments()).getRideId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onCreateView");
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ride_map, container, false);

        mAdapter = new RidePointMapRecyclerViewAdapter();
        mAdapter.setOnClickListener(mListener);
        RecyclerView recyclerView = mBinding.bottomSheets.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(mAdapter);

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
        setupUi();
    }

    @Override
    public void onStart() {
        super.onStart();
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
            requireActivity().unbindService(mServiceConnection);
            mBound = false;
        }
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupGoogleViewModel();
        setupRidesViewModel();
        setupRideViewModel();
        setupAuthViewModel();
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
        if(requestCode == 9){
            boolean permissionGranted = false;
            permissionGranted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
            if(!permissionGranted){
                Toast.makeText(requireActivity(), "You don't assign permission.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupRideViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        rideViewModel = new ViewModelProvider(requireActivity(), factory).get(RideViewModel.class);
        rideViewModel.getRideFinishResult().observe(getViewLifecycleOwner(),
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
                    rideViewModel.setRideFinishResult(null);
                });
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
                        if(result.getError() == R.string.error_401) { // Error 401: Unauthorized
                            authViewModel.logout(requireContext());
                        }
                    }
                    if(result.getSuccess()!=null){
                        setRide(result.getSuccess());
                    }
                    ridesViewModel.setRideResult(null);
                });
        ridesViewModel.getRideCompleteResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }
                    progressDialog.hide();

                    if(result.getError()!=null){
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText(getString(result.getError()))
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.dismissWithAnimation();
                                    progressDialog.show();
                                    ridesViewModel.complete(token, rideId);
                                })
                                .setCancelButton("Annuler", SweetAlertDialog::dismissWithAnimation)
                                .show();
                    }

                    if(result.getSuccess()!=null){
                        this.setRide(result.getSuccess());
                        new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setContentText("Votre course a terminé!")
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.dismissWithAnimation();
                                    NavHostFragment.findNavController(RideMapFragment.this).popBackStack();
                                })
                                .show();
                    }

                    ridesViewModel.setRideCompleteResult(null);
                });
    }

    private void showSweetError(String string) {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(string)
                .show();
    }

    private void setupAuthViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        loadRide();
                    }else{
                        mBinding.setIsLoading(false);
                    }
                });
    }

    private void loadRide() {
        ridesViewModel.loadRide(token, rideId);
        mBinding.setIsLoading(true);
    }

    private void setRide(RideWithDatas rideWithDatas1) {
        this.rideWithDatas = rideWithDatas1;
        if(rideWithDatas==null){
            return;
        }

        ClubAndPoint clubAndPoint = rideWithDatas.getClubAndPoint();
        if(clubAndPoint!=null){
            Point point = clubAndPoint.getPoint();
            Club club = clubAndPoint.getClub();
            if(club!=null && point!=null){
                drawClubMarker(point, club);

                if(mMap!=null){
                    LatLng latLng = point.toLatLng();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            }
        }

        if(rideWithDatas.getPoints()!=null) {
            mAdapter.setItems(rideWithDatas1.getPoints());
            updateStepView(rideWithDatas1.getPoints());
            scrollRecyclerView(rideWithDatas1.getPoints());
            drawPoints(rideWithDatas1.getPoints());
        }

        Ride ride = rideWithDatas.getRide();
        if(ride!=null){
            String direction = ride.getDirection();
            if(direction!=null){
                drawLine(direction);
            }

            if(Ride.STATUS_COMPLETABLE.equals(ride.getStatus())){
                mAdapter.addCompleting();
                mBinding.bottomSheets.stepView.go(mBinding.bottomSheets.stepView.getStepCount(), true);
                if(mBinding.bottomSheets.recyclerView.getLayoutManager()!=null) {
                    mBinding.bottomSheets.recyclerView.getLayoutManager().scrollToPosition(mAdapter.getItemCount());
                }
            }
        }
    }

    private void updateStepView(List<RidePointWithDatas> ridePointWithDatas) {
        if(ridePointWithDatas==null) return;
        mBinding.bottomSheets.stepView.setStepsNumber(ridePointWithDatas.size() + 1);
        int i = 0;
        for(RidePointWithDatas ridePointWithData:  ridePointWithDatas){
            RidePoint ridePoint = ridePointWithData.getRidePoint();
            if(ridePoint!=null && RidePoint.STATUS_NEXT.equals(ridePoint.getStatus())){
                mBinding.bottomSheets.stepView.go(ridePoint.getOrder()>0?ridePoint.getOrder()-1:0, true);
                break;
            }
            i++;
        }
    }

    private void scrollRecyclerView(List<RidePointWithDatas> ridePointWithDatas) {
        if(ridePointWithDatas==null) return;
        int i = 0;
        for(RidePointWithDatas ridePointWithData:  ridePointWithDatas){
            RidePoint ridePoint = ridePointWithData.getRidePoint();
            if(ridePoint!=null && RidePoint.STATUS_NEXT.equals(ridePoint.getStatus())){
                if(mBinding.bottomSheets.recyclerView.getLayoutManager()!=null) {
                    mBinding.bottomSheets.recyclerView.getLayoutManager().scrollToPosition(i);
                }
                break;
            }
            i++;
        }
    }

    private void drawClubMarker(Point point, Club club) {
        if(mMap==null){
            return;
        }

        if(club!=null){
            Picasso.get()
                    .load(Constants.getBaseUrl() + club.getImageUrl())
                    .resize(64,64)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Log.d(TAG, "drawClubMarker.onBitmapLoaded ");
                            if(mClubMarker!=null){
                                //mClubMarker.remove();
                            }
                            mClubMarker = MapUiUtils.drawClubMarker(requireContext(), mMap, point, club.getName(), bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Log.e(TAG, "drawClubMarker.onBitmapFailed ", e);
                            if(mClubMarker!=null){
                                //mClubMarker.remove();
                            }
                            LatLng latLng = point.toLatLng();
                            MarkerOptions options = new MarkerOptions(); // Creating MarkerOptions
                            options.position(latLng); // Setting the position of the marker
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            mClubMarker = mMap.addMarker(options);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            Log.d(TAG, "drawClubMarker.onPrepareLoad ");
                        }
                    });
        }else{
            if(mClubMarker!=null){
                //mClubMarker.remove();
            }

            LatLng latLng = point.toLatLng();
            MarkerOptions options = new MarkerOptions(); // Creating MarkerOptions
            options.position(latLng); // Setting the position of the marker
            options.icon(UiUtils.getBitmapFromMarkerView(requireContext(), point.getName()));
            mClubMarker = mMap.addMarker(options);
        }

    }

    private void drawPoints(List<RidePointWithDatas> points) {
        if(mMarkers==null){
            mMarkers = new ArrayList<>();
        }

        for(Marker marker: mMarkers){
            marker.remove();
        }
        mMarkers.clear();

        for(RidePointWithDatas ridePointWithDatas: points){
            Marker marker = drawPoint(ridePointWithDatas);
            if(marker!=null){
                mMarkers.add(marker);
            }
        }
    }

    private Marker drawPoint(RidePointWithDatas ridePointWithDatas) {
        if(mMap==null){
            return null;
        }

        Point point = ridePointWithDatas.getPoint();
        if(point==null){
            return null;
        }

        RidePoint ridePoint = ridePointWithDatas.getRidePoint();
        if(ridePoint==null){
            return null;
        }

        User customer = ridePointWithDatas.getUser();
        if(customer==null){
            return null;
        }

        return MapUiUtils.drawStepPoint(requireContext(), mMap, point, String.valueOf(ridePoint.getOrder()), customer.getName());
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
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

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
                        mMap.setMyLocationEnabled(true);
                        mService.requestLocationUpdates();
                    }
                }else{
                    // Remove live location update
                    Toast.makeText(requireContext(), "removeLocationUpdatesButton.click",
                            Toast.LENGTH_SHORT).show();
                    mMap.setMyLocationEnabled(false);
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


    private void phoneCall(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        startActivity(intent);
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

    private OnClickItemListener<RidePointWithDatas> mListener = (v, pos, ridePointWithDatas) -> {
        if(ridePointWithDatas==null) return;
        RidePoint ridePoint = ridePointWithDatas.getRidePoint();
        switch (v.getId()){
            case R.id.callButtom:
                if (ridePointWithDatas.getUser() != null && ridePointWithDatas.getUser().getPhone() != null) {
                    onCallBtnClick(ridePointWithDatas.getUser().getPhone());
                }
            break;
            case R.id.actionButton:
                if(ridePoint!=null) {
                    if (RidePoint.STATUS_NEXT.equals(ridePoint.getStatus())) {
                        progressDialog.show();
                        rideViewModel.finishRidePoint(token, ridePoint.getRid());
                    }
                }
                break;
            case R.id.cancelButton:
                if(ridePoint!=null) {
                    if(!RidePoint.STATUS_CANCELED.equals(ridePoint.getStatus())) {
                        progressDialog.show();
                        rideViewModel.cancelRidePoint(token, ridePoint.getRid());
                    }
                }
            break;
            case R.id.button_complete:
                progressDialog.show();
                ridesViewModel.complete(token, rideId);
            break;
        }
    };

}

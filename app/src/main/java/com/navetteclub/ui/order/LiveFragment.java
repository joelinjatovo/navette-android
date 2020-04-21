package com.navetteclub.ui.order;

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
import com.navetteclub.database.entity.Item;
import com.navetteclub.database.entity.ItemWithDatas;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.entity.RidePoint;
import com.navetteclub.database.entity.RidePointWithDatas;
import com.navetteclub.database.entity.User;
import com.navetteclub.databinding.FragmentLiveBinding;
import com.navetteclub.databinding.FragmentOrderMapBinding;
import com.navetteclub.services.LocationUpdatesService;
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
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private RideViewModel rideViewModel;

    private GoogleViewModel googleViewModel;

    private AuthViewModel authViewModel;

    private Marker myPositionMarker;

    private String token;

    private String itemId;

    private Polyline line1;

    private Marker mClubMarker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            token = LiveFragmentArgs.fromBundle(getArguments()).getToken();
            itemId = LiveFragmentArgs.fromBundle(getArguments()).getItemId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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
        setupUi();
        setupRideViewModel();
        setupGoogleViewModel();
        setupAuthViewModel();
    }

    private void setupRideViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        rideViewModel = new ViewModelProvider(requireActivity(), factory).get(RideViewModel.class);
        rideViewModel.getItemViewResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null) return;

                    if(result.getError()!=null){
                        Toast.makeText(requireContext(), result.getError(), Toast.LENGTH_SHORT).show();
                    }

                    if(result.getSuccess()!=null){
                        updateUi(result.getSuccess());
                    }
                });
    }

    private void updateUi(ItemWithDatas itemWithData) {
        if(itemWithData==null) return;
        User driver = itemWithData.getDriver();
        if(driver!=null){
            mBinding.setUser(driver);
            mBinding.nameTextView.setText(driver.getName());
            mBinding.roleTextView.setText(driver.getRole());
            if (driver.getImageUrl() != null) {
                Picasso.get()
                        .load(Constants.getBaseUrl() + driver.getImageUrl())
                        .placeholder(R.drawable.user_placeholder)
                        .error(R.drawable.user_placeholder)
                        .into(mBinding.avatarImageView);
            }
        }

        Item item = itemWithData.getItem();
        if(item!=null){
            String direction = item.getDirection();
            if(direction!=null){
                drawLine(direction);
            }
        }

        Point point = itemWithData.getPoint();
        if(point!=null){
            drawPoint(point, String.valueOf(1));
            if(mMap!=null){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point.toLatLng(), 15));
            }
        }
    }

    private void setupAuthViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        rideViewModel.loadItem(token, itemId);
                        User user = authViewModel.getUser();
                        connectPrivatePush();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
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
            requireActivity().unbindService(mServiceConnection);
            mBound = false;
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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

    private void setupGoogleViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        googleViewModel = new ViewModelProvider(requireActivity(), factory).get(GoogleViewModel.class);
    }

    private void updateUiWithOrder(OrderWithDatas orderWithDatas) {
        if(orderWithDatas==null) return;
        User user = orderWithDatas.getCarDriver();
        if(user!=null) {
            mBinding.setUser(user);
            mBinding.nameTextView.setText(user.getName());
            mBinding.roleTextView.setText(user.getRole());
            if (user.getImageUrl() != null) {
                Picasso.get()
                        .load(Constants.getBaseUrl() + user.getImageUrl())
                        .placeholder(R.drawable.user_placeholder)
                        .error(R.drawable.user_placeholder)
                        .into(mBinding.avatarImageView);
            }
        }

        Order order = orderWithDatas.getOrder();
        if(order!=null){
            mBinding.statusTextView.setText(order.getStatus());
        }

        Club club = orderWithDatas.getClub();
        Point clubPoint = orderWithDatas.getClubPoint();
        if(club!=null && clubPoint!=null){
            drawClubMarker(clubPoint, club);
        }

        if(clubPoint!=null){
            drawPoint(clubPoint, "0");
        }

        List<ItemWithDatas> itemWithDatas = orderWithDatas.getItems();
        if(itemWithDatas!=null){
            int i = 1;
            for(ItemWithDatas itemWithDatas1: itemWithDatas){
                if(itemWithDatas1!=null && itemWithDatas1.getItem()!=null){
                    Point point = itemWithDatas1.getPoint();
                    if(point!=null){
                        drawPoint(point, String.valueOf(i));
                        if(mMap!=null){
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point.toLatLng(), 15));
                        }
                    }
                    Item item = itemWithDatas1.getItem();
                    if(item!=null){
                        String direction = item.getDirection();
                        if(direction!=null){
                            drawLine(direction);
                        }
                    }
                }
            }
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

    private Marker drawPoint(Point point, String step) {
        return MapUiUtils.drawStepPoint(requireContext(), mMap, point, step, point.getName());
    }

    private void setupUi() {
        // Nothing
        mBinding.switchView.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if(isChecked){
                        // Live update location
                        if (!checkPermissions()) {
                            requestPermissions();
                        } else {
                            mService.requestLocationUpdates();
                        }
                    }else{
                        // Remove live location update
                        mService.removeLocationUpdates();
                    }
                });
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

    private void connectPrivatePush() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", token);
        HttpAuthorizer authorizer = new HttpAuthorizer(Constants.getBaseUrl() + "broadcasting/auth");
        authorizer.setHeaders(headers);
        PusherOptions options = new PusherOptions().setAuthorizer(authorizer);
        options.setCluster(Constants.getPusherAppCluster());
        Pusher pusher = new Pusher(Constants.getPusherAppKey(), options);
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.i(TAG + "Pusher", "Connection State Change: " + change.toString());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Log.i(TAG + "Pusher", String.format("Connection Error: [%s], exception was [%s]", message, e));
            }
        }, ConnectionState.ALL);

        pusher.subscribePrivate("private-App.Item."+ itemId, new PrivateChannelEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.d(TAG + "Pusher", "onEvent");
                Log.d(TAG + "Pusher", event.getEventName());
                Log.d(TAG + "Pusher", event.getData());
            }

            @Override
            public void onSubscriptionSucceeded(String channelName) {
                Log.d(TAG + "Pusher", "onSubscriptionSucceeded " + channelName);
            }

            @Override
            public void onAuthenticationFailure(String message, Exception e) {
                Log.d(TAG + "Pusher", String.format("Authentication failure due to [%s], exception was [%s]", message, e));
            }
        }, "item.created", "item.updated");
    }

}

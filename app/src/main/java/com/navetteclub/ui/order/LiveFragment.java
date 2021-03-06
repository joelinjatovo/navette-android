package com.navetteclub.ui.order;

import android.Manifest;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.navetteclub.App;
import com.navetteclub.BuildConfig;
import com.navetteclub.R;
import com.navetteclub.database.entity.ClubAndPoint;
import com.navetteclub.database.entity.Item;
import com.navetteclub.database.entity.ItemWithDatas;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.entity.Ride;
import com.navetteclub.database.entity.RidePoint;
import com.navetteclub.database.entity.User;
import com.navetteclub.database.entity.UserAndPoint;
import com.navetteclub.databinding.FragmentLiveBinding;
import com.navetteclub.services.LocationUpdatesService;
import com.navetteclub.ui.MainActivity;
import com.navetteclub.utils.Constants;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.MapUiUtils;
import com.navetteclub.utils.PusherOdk;
import com.navetteclub.utils.Utils;
import com.navetteclub.vm.AuthViewModel;
import com.navetteclub.vm.GoogleViewModel;
import com.navetteclub.vm.ItemViewModel;
import com.navetteclub.vm.LiveViewModel;
import com.navetteclub.vm.MyViewModelFactory;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

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

    private GoogleMap mMap;

    private Location mLastKnownLocation;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private FragmentLiveBinding mBinding;

    private ItemViewModel itemViewModel;

    private AuthViewModel authViewModel;

    private LiveViewModel liveViewModel;

    private Marker myPositionMarker;

    private Polyline line1;

    private Marker mClubMarker;

    private Pusher pusher;

    private Marker driverMarker;

    private String token;

    private String itemId;

    private ProgressDialog progressDialog;

    private Long rideId;

    private Handler handler = new Handler(Looper.getMainLooper());;

    private Runnable checkRunnable = this::check;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
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
        setupUI();
        setupItemViewModel();
        setupLiveViewModel();
        setupAuthViewModel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(pusher==null) return;
        if(pusher.getPrivateChannel("private-App.Item."+ itemId)!=null) {
            pusher.unsubscribe("private-App.Item."+ itemId);
        }

        if(rideId!=null && pusher.getPrivateChannel("private-App.Ride."+ rideId)!=null) {
            pusher.unsubscribe("private-App.Ride."+ rideId);

        }
    }

    private void setupUI() {
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.signing));

        mBinding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemId!=null && token!=null){
                    progressDialog.show();
                    itemViewModel.cancelItem(token, itemId);
                }
            }
        });
    }

    private void setupItemViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        itemViewModel = new ViewModelProvider(requireActivity(), factory).get(ItemViewModel.class);
        itemViewModel.getItemViewResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null) return;

                    if(result.getError()!=null){
                        Toast.makeText(requireContext(), result.getError(), Toast.LENGTH_SHORT).show();
                    }

                    if(result.getSuccess()!=null){
                        updateUi(result.getSuccess());

                        // Listen ride
                        ItemWithDatas itemWithData = result.getSuccess();
                        if(itemWithData==null) return;
                        Ride ride = itemWithData.getRide();
                        if(ride!=null){
                            rideId = ride.getId();
                            listenChannelDriverPosition(String.valueOf(rideId));
                        }
                    }

                    itemViewModel.setItemViewResult(null);
                });
        itemViewModel.getItemCancelResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null) return;

                    progressDialog.dismiss();

                    if(result.getError()!=null){
                        Toast.makeText(requireContext(), result.getError(), Toast.LENGTH_SHORT).show();
                    }

                    if(result.getSuccess()!=null){
                        updateUi(result.getSuccess());
                    }

                    itemViewModel.setItemCancelResult(null);
                });
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

    private void updateUi(ItemWithDatas itemWithData) {
        if(itemWithData==null) return;
        User driver = itemWithData.getDriver();
        if(driver!=null){
            mBinding.setUser(driver);
            mBinding.nameTextView.setText(driver.getName());
            mBinding.roleTextView.setText(driver.getRole());
            if (driver.getImageUrl() != null) {
                Picasso.get()
                        .load(BuildConfig.BASE_URL+ driver.getImageUrl())
                        .placeholder(R.drawable.user_placeholder)
                        .error(R.drawable.user_placeholder)
                        .into(mBinding.avatarImageView);
            }

            mBinding.buttonCall.setOnClickListener(
                    v -> {
                        if(driver.getPhone()!=null){
                            onCallBtnClick(driver.getPhone());
                        }
                    });
        }

        int color = R.color.colorAccent;
        Item item = itemWithData.getItem();
        String pointLabel = null;
        if(item!=null){
            if(item.getType()!=null){
                switch (item.getType()) {
                    case Item.TYPE_GO:
                        pointLabel = getString(R.string.type_pickup);
                        break;
                    case Item.TYPE_BACK:
                        pointLabel = getString(R.string.type_back);
                        break;
                }
            }
            if(item.getStatus()!=null){
                mBinding.setIsNext(false);
                mBinding.buttonCancel.setVisibility(View.VISIBLE);
                switch (item.getStatus()) {
                    case Item.STATUS_PING:
                        mBinding.statusTextView.setText(R.string.status_ping);
                        mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_default);
                        mBinding.statusTextView.setTextColor(getResources().getColor(R.color.white));
                        color = R.color.colorAlertError;
                        break;
                    case Item.STATUS_ACTIVE:
                        mBinding.statusTextView.setText(R.string.status_active);
                        mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_success);
                        mBinding.statusTextView.setTextColor(getResources().getColor(R.color.colorText));
                        color = R.color.colorImportant;
                        break;
                    case Item.STATUS_NEXT:
                        mBinding.setIsNext(true);
                        mBinding.setDate(Utils.formatDateToString(item.getRideAt()));
                        mBinding.statusTextView.setText(R.string.status_next);
                        mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_success);
                        mBinding.statusTextView.setTextColor(getResources().getColor(R.color.colorText));
                        color = R.color.colorImportant;
                        break;
                    case Item.STATUS_ARRIVED:
                        mBinding.statusTextView.setText(R.string.status_arrived);
                        mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_success);
                        mBinding.statusTextView.setTextColor(getResources().getColor(R.color.colorText));
                        color = R.color.colorImportant;
                        break;
                    case Item.STATUS_COMPLETED:
                        mBinding.buttonCancel.setVisibility(View.GONE);
                        mBinding.statusTextView.setText(R.string.status_completed);
                        mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_success);
                        mBinding.statusTextView.setTextColor(getResources().getColor(R.color.colorText));
                        color = R.color.gray;
                        break;
                    case Item.STATUS_CANCELED:
                        mBinding.buttonCancel.setVisibility(View.GONE);
                        mBinding.statusTextView.setText(R.string.status_canceled);
                        mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_error);
                        mBinding.statusTextView.setTextColor(getResources().getColor(R.color.white));
                        color = R.color.colorIcon;
                        break;
                    case Item.STATUS_ONLINE:
                        mBinding.statusTextView.setText(R.string.status_online);
                        mBinding.statusTextView.setBackgroundResource(R.drawable.bg_text_alert_success);
                        mBinding.statusTextView.setTextColor(getResources().getColor(R.color.colorText));
                        color = R.color.colorAccent;
                        break;
                }

            }

            String direction = null;

            List<RidePoint> ridePoints = itemWithData.getRidepoints();
            if(ridePoints!=null && ridePoints.size() > 0){
                RidePoint ridePoint = ridePoints.get(0);
                /* Draw ride point direction */
                //direction = ridePoint.getDirection();
            }

            //if(direction==null){
                /* Draw original direction */
                direction = item.getDirection();
            //}

            if(direction!=null){
                drawLine(direction);
            }
        }

        /* Draw ride direction */
        Ride ride = itemWithData.getRide();
        if(ride!=null){
            String direction = ride.getDirection();
            if(direction!=null){
                //drawLine(direction);
            }
        }

        Point point = itemWithData.getPoint();
        if(point!=null){
            if(pointLabel==null){
                pointLabel = point.getName();
            }
            drawPoint(point, String.valueOf(1), pointLabel, color);
            if(mMap!=null){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point.toLatLng(), Constants.MAP_ZOOM));
            }
        }

        ClubAndPoint clubAndPoint = itemWithData.getClubAndPoint();
        Log.e(TAG, "ClubANdPoint " + clubAndPoint);
        if(clubAndPoint!=null){
            drawClubMarker(clubAndPoint.getPoint());
        }
    }

    private void setupAuthViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        authViewModel = new ViewModelProvider(requireActivity(), factory).get(AuthViewModel.class);
        authViewModel.getAuthenticationState().observe(getViewLifecycleOwner(),
                authenticationState -> {
                    if (authenticationState == AuthViewModel.AuthenticationState.AUTHENTICATED) {
                        token = authViewModel.getUser().getAuthorizationToken();
                        check();
                        pusher = PusherOdk.getInstance(token).getPusher();
                        listenChannelItem(itemId);
                    }
                });
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(myReceiver);
        super.onPause();
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

    private void setupLiveViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        liveViewModel = new ViewModelProvider(requireActivity(), factory).get(LiveViewModel.class);
        liveViewModel.getDriverPositionLiveData().observe(getViewLifecycleOwner(),
                position -> {
                    if(position==null) return;
                    User driver = position.getUser();
                    if(driver==null) return;
                    Point point = position.getPoint();
                    if(point==null) return;
                    if(driverMarker!=null){
                        driverMarker.remove();
                    }
                    driverMarker = drawDriverPoint(point, driver);
                    liveViewModel.setDriverPositionLiveData(null);
                });
    }


    private void drawPoint(Point point, String step, String label, int color) {
        MapUiUtils.drawStepPoint(requireContext(), mMap, point, step, label, color);
    }

    private void drawClubMarker(Point point) {
        if(point==null) return;
        if(mMap==null) return;
        mClubMarker = MapUiUtils.drawTextPoint(requireContext(), mMap, point, getString(R.string.club));
    }


    private Marker drawDriverPoint(Point point, User driver) {
        if(point==null) return null;
        if(mMap==null) return null;
        return MapUiUtils.drawCarMarker(requireContext(), mMap, point, R.color.colorIcon);

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

    private void listenChannelDriverPosition(String rideId) {
        if(pusher==null) return;
        if(pusher.getPrivateChannel("private-App.Ride."+ rideId)==null) {
            pusher.subscribePrivate("private-App.Ride." + rideId, new PrivateChannelEventListener() {
                @Override
                public void onEvent(PusherEvent event) {
                    Log.d(TAG + "RidePusher", "onEvent");
                    Log.d(TAG + "RidePusher", event.getEventName());
                    Log.d(TAG + "RidePusher", event.getData());
                    Gson gson = new Gson();
                    UserAndPoint response = gson.fromJson(event.getData(), UserAndPoint.class);
                    requireActivity().runOnUiThread(() -> liveViewModel.setDriverPositionLiveData(response));
                }

                @Override
                public void onSubscriptionSucceeded(String channelName) {
                    Log.d(TAG + "RidePusher", "onSubscriptionSucceeded " + channelName);
                }

                @Override
                public void onAuthenticationFailure(String message, Exception e) {
                    Log.e(TAG + "RidePusher", String.format("Authentication failure due to [%s], exception was [%s]", message, e));
                }
            }, "user.point.created");
        }
    }

    private void listenChannelItem(String itemId) {
        if(pusher==null) return;
        if(pusher.getPrivateChannel("private-App.Item."+ itemId)==null) {
            pusher.subscribePrivate("private-App.Item." + itemId, new PrivateChannelEventListener() {
                @Override
                public void onEvent(PusherEvent event) {
                    Log.d(TAG + "ItemPusher", "onEvent");
                    Log.d(TAG + "ItemPusher", event.getEventName());
                    Log.d(TAG + "ItemPusher", event.getData());
                    check();
                    requireActivity().runOnUiThread(() -> {
                        try{
                            JSONObject jsonData = new JSONObject(event.getData());
                            String newStatus = jsonData.getString("newStatus");
                            switch (newStatus){
                                case Item.STATUS_PING:
                                    showSweetInfo(R.string.desc_order_item_status_ping);
                                    break;
                                case Item.STATUS_ACTIVE:
                                    showSweetInfo(R.string.desc_order_item_status_active);
                                    break;
                                case Item.STATUS_NEXT:
                                    showSweetInfo(R.string.desc_order_item_status_next);
                                    break;
                                case Item.STATUS_ONLINE:
                                    showSweetInfo(R.string.desc_order_item_status_online);
                                    break;
                                case Item.STATUS_CANCELED:
                                    showSweetInfo(R.string.desc_order_item_status_canceled);
                                    break;
                                case Item.STATUS_COMPLETED:
                                    showSweetInfo(R.string.desc_order_item_status_completed);
                                    break;
                            }
                        }catch (Exception ignored){}
                    });
                }

                @Override
                public void onSubscriptionSucceeded(String channelName) {
                    Log.d(TAG + "ItemPusher", "onSubscriptionSucceeded " + channelName);
                }

                @Override
                public void onAuthenticationFailure(String message, Exception e) {
                    Log.e(TAG + "ItemPusher", String.format("Authentication failure due to [%s], exception was [%s]", message, e));
                }
            }, "item.created", "item.updated");
        }
    }

    private void showSweetInfo(@StringRes int res){
        new SweetAlertDialog(requireContext(), SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(getString(R.string.info))
                .setContentText(getString(res))
                .show();
    }

    private void check() {
        handler.removeCallbacks(checkRunnable);
        handler.postDelayed(checkRunnable, 60*1000);
        itemViewModel.loadItem(token, itemId);
    }

    public static Uri getUri(String itemId){
        return Uri.parse("http://navetteclub.com/item/" + itemId);
    }

}

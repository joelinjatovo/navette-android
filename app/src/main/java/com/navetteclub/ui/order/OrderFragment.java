package com.navetteclub.ui.order;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.common.api.Status;
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
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.navetteclub.R;
import com.navetteclub.api.models.google.GoogleDirectionResponse;
import com.navetteclub.api.models.google.Leg;
import com.navetteclub.api.models.google.Route;
import com.navetteclub.api.services.GoogleApiService;
import com.navetteclub.database.entity.CarAndModel;
import com.navetteclub.database.entity.Point;
import com.navetteclub.databinding.FragmentOrderBinding;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.Utils;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = OrderFragment.class.getSimpleName();

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private GoogleMap mMap;

    private Location mLastKnownLocation;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private FragmentOrderBinding mBinding;

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

    private BottomSheetBehavior sheetBehavior;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onCreateView");

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        setupMap();
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
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG + "Cycle", "onDestroyView");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getLatLng() + ", " + place.getName() + ", " + place.getId());

                orderViewModel.setOrigin(place);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled the operation.");
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
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
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    mBinding.setShowMoreButton(true);
                } else {
                    mBinding.setShowMoreButton(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void setupOrderViewModel() {
        orderViewModel = new ViewModelProvider(this, new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);

        orderViewModel.getOrderWithDatasLiveData().observe(getViewLifecycleOwner(),
                orderWithDatas -> {
                    if(orderWithDatas == null){
                        return;
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
        mBinding.originEndIcon.setOnClickListener(
                v -> {
                    getDeviceLocation();
                });

        mBinding.originText.setOnClickListener(
                v -> {
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

                    // Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.OVERLAY, fields)
                            .build(requireContext());
                    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                });

        mBinding.destinationText.setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_order_to_clubs);
                });

        mBinding.destinationEndIcon.setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_order_to_clubs);
                });

        mBinding.moreButton.setOnClickListener(
                v -> {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                });

        mBinding.bottomSheets.bookNowButton.setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_order_to_cars);
                });
    }

    private void expandOrderDetails() {
        if(sheetBehavior != null && mOrigin != null && mDestination != null){
            mBinding.setIsLoadingDirection(true);
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }else{
            mBinding.setIsLoadingDirection(false);
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
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
                                orderViewModel.setOrigin(getString(R.string.my_location), latLng);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            }
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
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

    private void getDirectionAndDistance(String type) {
        if(mOrigin == null ){
            return;
        }

        if(mDestination == null ){
            return;
        }

        if(mOldOrigin != null && mOrigin.latitude == mOldOrigin.latitude && mOrigin.longitude == mOldOrigin.longitude){
            return;
        }

        if(mOldDestination != null  && mDestination.latitude == mOldDestination.latitude && mDestination.longitude == mOldDestination.longitude){
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

        GoogleApiService service = retrofit.create(GoogleApiService.class);
        Call<GoogleDirectionResponse> call = service.getDirection(
                key,
                "metric",
                mOrigin.latitude + "," + mOrigin.longitude,
                mDestination.latitude + "," + mDestination.longitude,
                type
        );

        // Show loader
        expandOrderDetails();

        call.enqueue(new Callback<GoogleDirectionResponse>() {
            @Override
            public void onResponse(@NonNull Call<GoogleDirectionResponse> call, @NonNull Response<GoogleDirectionResponse> response) {
                // Hide loader
                mBinding.setIsLoadingDirection(false);

                try {
                    //Remove previous line from map
                    if (line != null) {
                        line.remove();
                    }
                    // This loop will go through all the results and add marker on each location.
                    GoogleDirectionResponse googleDirectionResponse = response.body();

                    Log.e(TAG, "googleDirectionResponse = " + googleDirectionResponse);
                    for (int i = 0; i < googleDirectionResponse.getRoutes().size(); i++) {
                        Route route = googleDirectionResponse.getRoutes().get(i);
                        for(Leg leg: route.getLegs()){
                            String distance = leg.getDistance().getText();
                            String time = leg.getDuration().getText();

                            mBinding.setDistance(distance);
                            mBinding.setDelay(time);

                            Log.d(TAG, String.format("Distance:%s, Duration:%s", distance, time));

                        }

                        String encodedString = route.getOverviewPolyline().getPoints();
                        List<LatLng> list = Utils.decodePoly(encodedString);
                        line = mMap.addPolyline(new PolylineOptions()
                                .addAll(list)
                                .width(5)
                                .color(R.color.colorAccent)
                                .geodesic(true)
                        );
                    }

                    mBinding.setShowMoreButton(true);

                } catch (Exception e) {
                    Log.e(TAG, "There is an error", e);
                    mBinding.setShowMoreButton(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GoogleDirectionResponse> call, @NonNull Throwable t) {
                mBinding.setIsLoadingDirection(false);
                mBinding.setShowMoreButton(false);
                Log.e(TAG, t.toString(), t);
            }
        });

    }

    private OnListFragmentInteractionListener mListener = new OnListFragmentInteractionListener() {
        @Override
        public void onListFragmentInteraction(View v, int position, CarAndModel item) {
            Navigation.findNavController(v).navigate(R.id.cars_fragment);
        }
    };

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(View view, int position, CarAndModel item);
    }

}

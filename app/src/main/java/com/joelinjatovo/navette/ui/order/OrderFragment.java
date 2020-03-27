package com.joelinjatovo.navette.ui.order;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.api.models.google.GoogleDirectionResponse;
import com.joelinjatovo.navette.api.models.google.Leg;
import com.joelinjatovo.navette.api.models.google.Route;
import com.joelinjatovo.navette.api.services.GoogleApiService;
import com.joelinjatovo.navette.database.entity.CarAndModel;
import com.joelinjatovo.navette.database.entity.ClubAndPoint;
import com.joelinjatovo.navette.databinding.FragmentOrderBinding;
import com.joelinjatovo.navette.utils.Log;
import com.joelinjatovo.navette.utils.Utils;
import com.joelinjatovo.navette.vm.MyViewModelFactory;
import com.joelinjatovo.navette.vm.OrderViewModel;

import java.util.ArrayList;
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

    private ClubAndPoint mClubAndPoint;

    private Polyline line;

    private CarRecyclerViewAdapter mAdapter;

    private LatLng mOrigin;

    private LatLng mDestination;

    private BottomSheetBehavior sheetBehavior;

    private ConstraintLayout bottom_sheet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false);

        // Set the adapter
        mAdapter = new CarRecyclerViewAdapter(mListener);
        RecyclerView recyclerView = mBinding.getRoot().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(mAdapter);

        bottom_sheet = mBinding.getRoot().findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // click event for show-dismiss bottom sheet
        mBinding.showCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

        // callback for do something
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN: {
                        mBinding.showCarButton.setText("Expand Sheet");
                        mBinding.setShowChooseCarButton(true);
                    }
                    break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        mBinding.setShowChooseCarButton(false);
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


        orderViewModel = new ViewModelProvider(this, new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);

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

        orderViewModel.getClub().observe(getViewLifecycleOwner(),
                clubAndPoint -> {
                    if (clubAndPoint == null){
                        return;
                    }

                    mClubAndPoint = clubAndPoint;

                    Log.d(TAG, clubAndPoint.getClub().getName());

                    Log.d(TAG, "Origin " + mOrigin);

                    mBinding.destinationText.setText(clubAndPoint.getClub().getName());

                    LatLng latLng = new LatLng(clubAndPoint.getPoint().getLat(),clubAndPoint.getPoint().getLng());
                    orderViewModel.setDestination(latLng);
                    if(mMap!=null){
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    }
                });

        orderViewModel.getDestination().observe(getViewLifecycleOwner(), latLng -> {
            mOrigin = latLng;
            getDirectionAndDistance("driving");
        });

        orderViewModel.getOrigin().observe(getViewLifecycleOwner(), latLng -> {
            mDestination = latLng;
            getDirectionAndDistance("driving");
        });

        orderViewModel.getOriginText().observe(getViewLifecycleOwner(), label -> {
            mBinding.originText.setText(label);
        });

        orderViewModel.getRetrofitResult().observe(getViewLifecycleOwner(), listRemoteLoaderResult -> {
            if(listRemoteLoaderResult == null){
                return;
            }

            if(listRemoteLoaderResult.getError()!=null){
                // show error
                Toast.makeText(requireContext(), listRemoteLoaderResult.getError(), Toast.LENGTH_SHORT).show();
            }

            if(listRemoteLoaderResult.getSuccess()!=null){
                // show car list
                Toast.makeText(requireContext(), "Success loading cars", Toast.LENGTH_SHORT).show();
            }

        });

        orderViewModel.getCars().observe(getViewLifecycleOwner(), carAndModels -> {
            if(carAndModels == null || mAdapter == null){
                return;
            }

            mAdapter.setItems(carAndModels);

            if(sheetBehavior != null){
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getLatLng() + ", " + place.getName() + ", " + place.getId());

                orderViewModel.setOriginText(place.getName());
                orderViewModel.setOrigin(place.getLatLng());
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
                                orderViewModel.setOriginText(getString(R.string.my_location));
                                orderViewModel.setOrigin(latLng);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            }
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

        call.enqueue(new Callback<GoogleDirectionResponse>() {
            @Override
            public void onResponse(@NonNull Call<GoogleDirectionResponse> call, @NonNull Response<GoogleDirectionResponse> response) {
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
                            //mBinding.durationTextView.setText(String.format("Distance:%s, Duration:%s", distance, time));
                            Log.d(TAG, String.format("Distance:%s, Duration:%s", distance, time));
                        }
                        String encodedString = googleDirectionResponse.getRoutes().get(0).getOverviewPolyline().getPoints();
                        List<LatLng> list = decodePoly(encodedString);
                        line = mMap.addPolyline(new PolylineOptions()
                                .addAll(list)
                                .width(5)
                                .color(R.color.colorAccent)
                                .geodesic(true)
                        );
                    }
                } catch (Exception e) {
                    Log.e(TAG, "There is an error", e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GoogleDirectionResponse> call, @NonNull Throwable t) {
                Log.e(TAG, t.toString(), t);
            }
        });

    }
    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
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

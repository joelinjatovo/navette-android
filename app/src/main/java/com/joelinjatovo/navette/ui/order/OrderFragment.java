package com.joelinjatovo.navette.ui.order;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

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
import com.google.android.material.snackbar.Snackbar;
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.api.models.google.GoogleDirectionResponse;
import com.joelinjatovo.navette.api.services.GoogleApiService;
import com.joelinjatovo.navette.database.entity.CarAndModel;
import com.joelinjatovo.navette.database.entity.ClubAndPoint;
import com.joelinjatovo.navette.databinding.FragmentOrderBinding;
import com.joelinjatovo.navette.utils.Log;
import com.joelinjatovo.navette.utils.Utils;
import com.joelinjatovo.navette.vm.MyViewModelFactory;
import com.joelinjatovo.navette.vm.OrderViewModel;

import java.util.ArrayList;
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

    private GoogleMap mMap;

    private Location mLastKnownLocation;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private FragmentOrderBinding mBinding;

    private OrderViewModel orderViewModel;

    private ClubAndPoint mClubAndPoint;

    private Polyline line;

    ArrayList<LatLng> markerPoints;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false);
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

        orderViewModel = new ViewModelProvider(this, new MyViewModelFactory(requireActivity().getApplication())).get(OrderViewModel.class);

        mBinding.originLocationTextInputLayout.setEndIconOnClickListener(
                v->{
                        getDeviceLocation();
                });

        mBinding.destinationLocationTextInputLayout.setEndIconOnClickListener(
                v->{
                        Navigation.findNavController(v).navigate(R.id.action_order_to_clubs);
                });

        orderViewModel.getClub().observe(getViewLifecycleOwner(),
                clubAndPoint -> {
                    if (clubAndPoint == null){
                        return;
                    }

                    mClubAndPoint = clubAndPoint;

                    Log.d(TAG, clubAndPoint.getClub().getName());

                    mBinding.destinationLocationTextInputEditText.setText(clubAndPoint.getClub().getName());

                    getDirectionAndDistance("driving");

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

                                mBinding.originLocationTextInputEditText.setText(R.string.my_location);

                                getDirectionAndDistance("driving");
                            }
                        } else {
                            //Log.d(TAG, "Current location is null. Using defaults.");
                            //Log.e(TAG, "Exception: %s", task.getException());
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
        if(mLastKnownLocation == null ){
            return;
        }
        if(mClubAndPoint == null ){
            return;
        }

        LatLng origin = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        LatLng destination = new LatLng(mClubAndPoint.getPoint().getLat(), mClubAndPoint.getPoint().getLng());

        String url = "https://maps.googleapis.com/maps/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        String key = getString(R.string.google_maps_key);

        GoogleApiService service = retrofit.create(GoogleApiService.class);
        Call<GoogleDirectionResponse> call = service.getDirection(key,"metric", origin.latitude + "," + origin.longitude,destination.latitude + "," + destination.longitude, type);
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
                        String distance = googleDirectionResponse.getRoutes().get(i).getLegs().get(i).getDistance().getText();
                        String time = googleDirectionResponse.getRoutes().get(i).getLegs().get(i).getDuration().getText();
                        //mBinding.durationTextView.setText(String.format("Distance:%s, Duration:%s", distance, time));
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

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(CarAndModel item);
    }

}

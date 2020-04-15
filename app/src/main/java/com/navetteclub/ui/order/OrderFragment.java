package com.navetteclub.ui.order;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
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
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.navetteclub.R;
import com.navetteclub.api.models.google.Leg;
import com.navetteclub.api.models.google.Route;
import com.navetteclub.database.entity.CarAndModel;
import com.navetteclub.database.entity.Club;
import com.navetteclub.database.entity.Point;
import com.navetteclub.databinding.FragmentOrderBinding;
import com.navetteclub.ui.OnClickItemListener;
import com.navetteclub.utils.Constants;
import com.navetteclub.utils.Log;
import com.navetteclub.utils.UiUtils;
import com.navetteclub.utils.Utils;
import com.navetteclub.vm.GoogleViewModel;
import com.navetteclub.vm.MyViewModelFactory;
import com.navetteclub.vm.OrderViewModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class OrderFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = OrderFragment.class.getSimpleName();

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;

    private static final float MAP_ZOOM = 15;

    private GoogleMap mMap;

    private Location mLastKnownLocation;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private FragmentOrderBinding mBinding;

    private OrderViewModel orderViewModel;

    private GoogleViewModel googleViewModel;

    private Polyline line;

    private LatLng mOrigin;

    private LatLng mDestination;

    private LatLng mRetours;

    private Marker mOriginMarker;

    private Marker mDestinationMarker;

    private Marker mRetoursMarker;

    private BottomSheetBehavior sheetBehavior;

    private CarRecyclerViewAdapter mAdapter;

    private OnClickItemListener<CarAndModel> mListerner = new OnClickItemListener<CarAndModel>() {
        @Override
        public void onClick(View v, int position, CarAndModel item) {
            Toast.makeText(getContext(), "clic", Toast.LENGTH_SHORT).show();
            List<CarAndModel> models = mAdapter.getItems();
            for(CarAndModel model: models){
                model.setSelected(false);
            }
            if(models.size()>position) {
                models.get(position).setSelected(true);
            }
            orderViewModel.setCar(item.getCar());
            mAdapter.setItems(models);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onCreateView");

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false);

        mAdapter = new CarRecyclerViewAdapter(mListerner);
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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG + "Cycle", "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        setupBottomSheet();
        setupUi();
        setupOrderViewModel();
        setupGoogleViewModel();
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
                orderViewModel.setOrigin(place, true);
                Log.i(TAG, "Place: " + place.getLatLng() + ", " + place.getName() + ", " + place.getId());
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
        //sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void setupGoogleViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        googleViewModel = new ViewModelProvider(requireActivity(), factory).get(GoogleViewModel.class);
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
                    if(originPoint==null){
                        getDeviceLocation(SearchType.ORIGIN);
                        return;
                    }
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
                    if(destinationPoint==null){
                        return;
                    }
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
                    if(retoursPoint==null){
                        return;
                    }
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
                    //Club
                    /*
                    if(orderWithDatas.getClub()!=null){
                        mBinding.bottomSheets.setIsLoadingCar(true);
                        orderViewModel.loadCars(orderWithDatas.getClub());
                    }
                     */
                });

        orderViewModel.getCarsResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result == null){
                        return;
                    }
                    mBinding.bottomSheets.setIsLoadingCar(false);
                    if(result.getError()!=null){
                        // Error loading
                        mBinding.bottomSheets.setShowErrorLoaderCar(true);
                        Toast.makeText(requireContext(), result.getError(), Toast.LENGTH_SHORT).show();
                    }
                    if(result.getSuccess()!=null){
                        ArrayList<CarAndModel> items = (ArrayList<CarAndModel>) result.getSuccess();
                        if(items.isEmpty()){
                            mBinding.bottomSheets.setShowErrorLoaderCar(true);
                        }else{
                            mBinding.bottomSheets.setShowErrorLoaderCar(false);
                            mAdapter.setItems(items);
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
        mBinding.originEndIcon.setOnClickListener(
                v -> {
                    getDeviceLocation(SearchType.ORIGIN);
                });
        mBinding.originText.setOnClickListener(
                v -> {
                    OrderFragmentDirections.ActionOrderToSearch action = OrderFragmentDirections.actionOrderToSearch();
                    action.setSearchType(SearchType.ORIGIN);
                    Navigation.findNavController(v).navigate(action);
                });
        mBinding.retoursText.setOnClickListener(
                v -> {
                    OrderFragmentDirections.ActionOrderToSearch action = OrderFragmentDirections.actionOrderToSearch();
                    action.setSearchType(SearchType.RETOURS);
                    Navigation.findNavController(v).navigate(action);
                });
        mBinding.retoursEndIcon.setOnClickListener(
                v -> {
                    orderViewModel.setReturn((Point) null, true);
                });
        mBinding.destinationText.setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_order_to_clubs);
                });
        mBinding.destinationEndIcon.setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_order_to_clubs);
                });
        mBinding.bottomSheets.bookNowButton.setOnClickListener(
                v -> {
                    Navigation.findNavController(v).navigate(R.id.action_order_fragment_to_place_fragment);
                    //Navigation.findNavController(v).navigate(R.id.action_order_to_cars);
                });
        mBinding.bottomSheets.privatizeSwitchView.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    orderViewModel.setPrivatized(isChecked);
                });
        mBinding.bottomSheets.buttonRefreshCar.setOnClickListener(
                v -> {
                    mBinding.bottomSheets.setIsLoadingCar(true);
                    mBinding.bottomSheets.setShowErrorLoaderCar(false);
                    orderViewModel.loadCars();
                });
    }

    private void expandOrderDetails() {
        if(sheetBehavior != null && mOrigin != null && mDestination != null){
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    private void getDeviceLocation(SearchType searchType) {
        try {
            if (checkPermissions()) {
                Task locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(),
                        task -> {
                            Log.d(TAG, "locationResult.addOnCompleteListener");
                            if (task.isSuccessful()) {
                                mLastKnownLocation = (Location) task.getResult();
                                if (mLastKnownLocation != null) {
                                    LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                    if(searchType == SearchType.ORIGIN){
                                        orderViewModel.setOrigin(getString(R.string.my_location), latLng, true);
                                    }else{
                                        orderViewModel.setReturn(getString(R.string.my_location), latLng, true);
                                    }
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

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(View view, int position, CarAndModel item);
    }

}

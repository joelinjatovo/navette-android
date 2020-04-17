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
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.CarAndModel;
import com.navetteclub.database.entity.Club;
import com.navetteclub.database.entity.Item;
import com.navetteclub.database.entity.ItemWithDatas;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.entity.Order;
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

import retrofit2.Call;


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

    private GoogleViewModel googleViewModel1;

    private GoogleViewModel googleViewModel2;

    private Polyline lineGo;

    private Polyline lineBack;

    private Marker item1Marker;

    private Marker clubMarker;

    private Marker item2Marker;

    private BottomSheetBehavior sheetBehavior;

    private CarRecyclerViewAdapter mAdapter;

    private Call<RetrofitResponse<OrderWithDatas>> cartRetrofitRequest;

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
            orderViewModel.setCarLiveData(item.getCar());
            mAdapter.setItems(models);

            mAdapter.notifyItemChanged(mAdapter.getSelected());
            mAdapter.notifyItemChanged(position);
            mAdapter.setSelected(position);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false);

        mAdapter = new CarRecyclerViewAdapter(mListerner);
        RecyclerView recyclerView = mBinding.bottomSheets.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupMap();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUi();
        setupBottomSheet();
    }

    private void setupBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from(mBinding.bottomSheets.bottomSheet);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupOrderViewModel();
        setupGoogleViewModel();
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
        googleViewModel1 = new ViewModelProvider(requireActivity(), factory).get(GoogleViewModel.class);
        googleViewModel1.getDirectionResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }
                    mBinding.setIsLoadingDirection(false);
                    mBinding.setShowErrorLoader(false);
                    if(result.body()!=null){
                        for (int i = 0; i < result.body().getRoutes().size(); i++) {
                            Route route = result.body().getRoutes().get(i);
                            String direction = route.getOverviewPolyline().getPoints();
                            for(Leg leg: route.getLegs()){
                                Item item = orderViewModel.getItem1();
                                if(item==null){
                                    item = new Item();
                                }
                                item.setDelay(leg.getDuration().getText());
                                item.setDelayValue(leg.getDuration().getValue());
                                item.setDistance(leg.getDistance().getText());
                                item.setDistanceValue(leg.getDistance().getValue());
                                item.setDirection(direction);
                                orderViewModel.setItem1LiveData(item);
                            }
                        }
                    }
                });
        googleViewModel1.getErrorResult().observe(getViewLifecycleOwner(),
                error -> {
                    if(error==null){
                        return;
                    }
                    mBinding.setIsLoadingDirection(false);
                    showDirectionError(error);
                });

        googleViewModel2 = new ViewModelProvider(requireActivity(), factory).get(GoogleViewModel.class);
        googleViewModel2.getDirectionResult().observe(getViewLifecycleOwner(),
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
                                Item item = orderViewModel.getItem2();
                                if(item==null){
                                    item = new Item();
                                }
                                item.setDelay(leg.getDuration().getText());
                                item.setDelayValue(leg.getDuration().getValue());
                                item.setDistance(leg.getDistance().getText());
                                item.setDistanceValue(leg.getDistance().getValue());
                                orderViewModel.setItem2LiveData(item);
                            }
                        }
                    }
                });
    }

    private void showDirectionError(String error) {
        mBinding.setShowErrorLoader(false);
    }

    private void setupOrderViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        orderViewModel = new ViewModelProvider(this, factory).get(OrderViewModel.class);
        orderViewModel.getOriginLiveData().observe(getViewLifecycleOwner(), value -> mBinding.setOrigin(value));
        orderViewModel.getDestinationLiveData().observe(getViewLifecycleOwner(), value -> mBinding.setDestination(value));
        orderViewModel.getBackLiveData().observe(getViewLifecycleOwner(), value -> mBinding.setBack(value));
        orderViewModel.getItem1PointLiveData().observe(getViewLifecycleOwner(),
                point -> {
                    Log.d(TAG+"Point", "Changement du Point1");
                    Point origin = orderViewModel.getOriginPoint();
                    Point destination = orderViewModel.getDestinationPoint();
                    Log.d(TAG+"Point", "Changement du Point1 [origin] " + origin);
                    Log.d(TAG+"Point", "Changement du Point1 [destination] " + destination);
                    if(origin!=null && destination!=null){
                        loadDirection(googleViewModel1, origin.toLatLng(), destination.toLatLng());
                    }
                    if(point!=null){
                        item1Marker = drawItemMarker(item1Marker, point);
                    }
                });
        orderViewModel.getClubPointLiveData().observe(getViewLifecycleOwner(),
                point -> {
                    Log.d(TAG+"Point", "Changement du point de club");
                    Point origin = orderViewModel.getOriginPoint();
                    Point destination = orderViewModel.getDestinationPoint();
                    Point back = orderViewModel.getBackPoint();
                    Log.d(TAG+"Point", "Changement du point de club [origin]" + origin);
                    Log.d(TAG+"Point", "Changement du point de club [destination]" + destination);
                    Log.d(TAG+"Point", "Changement du point de club [back]" + back);
                    if(origin!=null && destination!=null){
                        loadDirection(googleViewModel1, origin.toLatLng(), destination.toLatLng());
                    }
                    if(back!=null && destination!=null){
                        loadDirection(googleViewModel2, destination.toLatLng(), back.toLatLng());
                    }
                    Club club = orderViewModel.getClub();
                    Log.d(TAG, "getClubPointLiveData.club" + club);
                    Log.d(TAG, "getClubPointLiveData.point" + point);
                    if(point!=null && club!=null){
                        drawClubMarker(point, club);
                    }
                });
        orderViewModel.getItem2PointLiveData().observe(getViewLifecycleOwner(),
                point -> {
                    Log.d(TAG+"Point", "Changement du Point2");
                    Point destination = orderViewModel.getDestinationPoint();
                    Point back = orderViewModel.getBackPoint();
                    Log.d(TAG+"Point", "Changement du Point2 [destination] " + destination);
                    Log.d(TAG+"Point", "Changement du Point2 [back] " + back);
                    if(back!=null && destination!=null){
                        loadDirection(googleViewModel2, destination.toLatLng(), back.toLatLng());
                    }
                    if(point!=null){
                        item2Marker = drawItemMarker(item2Marker, point);
                    }
                });
        orderViewModel.getItem1LiveData().observe(getViewLifecycleOwner(),
                item -> {
                    Log.d(TAG+"Item", "Changement du Item1 " + item);
                    if(item!=null){
                        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        mBinding.bottomSheets.setDelay(item.getDelay());
                        mBinding.bottomSheets.setDistance(item.getDistance());
                        String direction = item.getDirection();
                        Log.d(TAG+"Direction", "direction == " + direction);
                        if(direction!=null && mMap!=null) {
                            List<LatLng> points = Utils.decodePoly(direction);
                            Log.d(TAG+"Direction", "decodePoly == " + points);
                            //Remove previous line from map
                            if (lineGo != null) lineGo.remove();
                            lineGo = mMap.addPolyline(new PolylineOptions()
                                    .addAll(points)
                                    .width(5)
                                    .color(R.color.colorAccent)
                                    .geodesic(true)
                            );
                        }

                    }
                });
        orderViewModel.getItem2LiveData().observe(getViewLifecycleOwner(),
                item -> {
                    Log.d(TAG+"Item", "Changement du Item2 " + item);
                    if(item!=null){
                        // @TODO
                        String direction = item.getDirection();
                        Log.d(TAG+"Direction", "getItem2LiveData " + direction);
                        if(direction!=null && mMap!=null) {
                            List<LatLng> points = Utils.decodePoly(direction);
                            //Remove previous line from map
                            if (lineBack != null) lineBack.remove();
                            lineBack = mMap.addPolyline(new PolylineOptions()
                                    .addAll(points)
                                    .width(5)
                                    .color(R.color.colorAlert)
                                    .geodesic(true)
                            );
                        }
                    }
                });
        orderViewModel.getClubLiveData().observe(getViewLifecycleOwner(),
                club -> {
                    Log.d(TAG+"Item", "Changement du club");
                    if(club!=null){
                        mBinding.bottomSheets.setIsLoadingCar(true);
                        mBinding.bottomSheets.setShowErrorLoaderCar(false);
                        orderViewModel.loadCars(club);
                    }
                    Point point = orderViewModel.getClubPoint();
                    if(point!=null && club!=null){
                        drawClubMarker(point, club);
                    }
                });
        orderViewModel.getItem1LiveData().observe(getViewLifecycleOwner(),
                item1 -> {
                    if(item1==null) return;
                    loadCart();
                });
        orderViewModel.getItem2LiveData().observe(getViewLifecycleOwner(),
                item2 -> {
                    if(item2==null) return;
                    loadCart();
                });
        orderViewModel.getClubLiveData().observe(getViewLifecycleOwner(),
                club -> {
                    if(club==null) return;
                    loadCart();
                });
        orderViewModel.getOrderLiveData().observe(getViewLifecycleOwner(),
                order -> {
                    if(order!=null) {
                        mBinding.bottomSheets.setPlace(order.getPlace());
                        loadCart();
                    }
                });
        orderViewModel.getCartResult().observe(getViewLifecycleOwner(),
                cart -> {
                    if(cart!=null) {
                        if(cart.getError()!=null){
                            // @TODO
                        }
                        if(cart.getSuccess()!=null){
                            OrderWithDatas data = cart.getSuccess();
                            if(data!=null){
                                Order order = data.getOrder();
                                if(order!=null) {
                                    //orderViewModel.setOrderLiveData(order);
                                    mBinding.setAmount(order.getAmountStr());
                                }
                            }
                        }
                    }
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
        orderViewModel.getOrderResult().observe(getViewLifecycleOwner(),
                result -> {
                    if(result == null){
                        return;
                    }
                    if(result.getError()!=null){
                        // Error loading
                        Toast.makeText(requireContext(), result.getError(), Toast.LENGTH_SHORT).show();
                    }
                    if(result.getSuccess()!=null){
                        Order order = result.getSuccess().getOrder();
                        if(order!=null){
                            mBinding.bottomSheets.setAmount(order.getAmountStr());
                        }
                    }
                });
    }

    private void loadCart() {
        Club club = orderViewModel.getClub();
        if(club==null) return;
        Item item = orderViewModel.getItem1();
        if(item==null) return;
        if(cartRetrofitRequest!=null){
            cartRetrofitRequest.cancel();
        }
        cartRetrofitRequest = orderViewModel.getCart();
    }

    private void loadDirection(GoogleViewModel googleViewModel, LatLng origin, LatLng destination) {
        if( (origin == null) || (destination == null)){
            return;
        }
        mBinding.setIsLoading(true);
        mBinding.setShowErrorLoader(false);
        googleViewModel.loadDirection(getString(R.string.google_maps_key), origin, destination);
    }

    private Marker drawItemMarker(Marker marker, Point point) {
        if(mMap==null){
            return marker;
        }

        if(marker!=null){
            marker.remove();
        }

        LatLng latLng = point.toLatLng();
        MarkerOptions options = new MarkerOptions(); // Creating MarkerOptions
        options.position(latLng); // Setting the position of the marker
        options.icon(UiUtils.getBitmapFromMarkerView(requireContext(), point.getName()));
        //options.anchor(1, 0.5f);

        return mMap.addMarker(options);
    }

    private void drawClubMarker(Point point, Club club) {
        if(mMap==null){
            return;
        }

        if(clubMarker!=null){
            clubMarker.remove();
        }

        LatLng latLng = point.toLatLng();
        MarkerOptions options = new MarkerOptions(); // Creating MarkerOptions
        options.position(latLng); // Setting the position of the marker
        if(club!=null){
            Picasso.get()
                    .load(Constants.getBaseUrl() + club.getImageUrl())
                    .resize(64,64)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Log.d(TAG, "drawClubMarker.onBitmapLoaded ");
                            // loaded bitmap is here (bitmap)
                            options.icon(UiUtils.getBitmapFromMarkerView(requireContext(), club.getName(), bitmap));
                            //options.anchor(0.5f, 1);
                            clubMarker = mMap.addMarker(options);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            Log.e(TAG, "drawClubMarker.onBitmapFailed ", e);
                            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                            clubMarker = mMap.addMarker(options);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            Log.d(TAG, "drawClubMarker.onPrepareLoad ");
                        }
                    });
        }else{
            options.icon(UiUtils.getBitmapFromMarkerView(requireContext(), point.getName()));
            clubMarker = mMap.addMarker(options);
        }
    }

    private void setupUi() {
        mBinding.swapPoints.setOnClickListener(v -> orderViewModel.swap());
        mBinding.originText.setOnClickListener(
                v -> {
                    OrderType orderType = orderViewModel.getOrderType();
                    if (orderType == OrderType.BACK) {
                        Navigation.findNavController(v).navigate(R.id.action_order_to_clubs);
                    } else {
                        OrderFragmentDirections.ActionOrderToSearch action = OrderFragmentDirections.actionOrderToSearch();
                        action.setSearchType(SearchType.ORIGIN);
                        Navigation.findNavController(v).navigate(action);
                    }
                });
        mBinding.destinationText.setOnClickListener(
                v -> {
                    OrderType orderType = orderViewModel.getOrderType();
                    if (orderType == OrderType.BACK) {
                        OrderFragmentDirections.ActionOrderToSearch action = OrderFragmentDirections.actionOrderToSearch();
                        action.setSearchType(SearchType.ORIGIN);
                        Navigation.findNavController(v).navigate(action);
                    }else{
                        Navigation.findNavController(v).navigate(R.id.action_order_to_clubs);
                    }
                });
        mBinding.retoursText.setOnClickListener(
                v -> {
                    OrderFragmentDirections.ActionOrderToSearch action = OrderFragmentDirections.actionOrderToSearch();
                    action.setSearchType(SearchType.RETOURS);
                    Navigation.findNavController(v).navigate(action);
                });
        mBinding.clearRetours.setOnClickListener(
                v -> {
                    orderViewModel.setOrderTypeLiveData(OrderType.GO);
                    orderViewModel.setItem2LiveData((Item) null);
                    orderViewModel.setItem2PointLiveData(null);
                });
        mBinding.bottomSheets.bookNowButton.setOnClickListener(
                v -> {
                    Log.d(TAG, "bookNowButton() = " + orderViewModel.getOrderType());
                    if(orderViewModel.getOrderType()==OrderType.GO){
                        Navigation.findNavController(v).navigate(R.id.action_order_fragment_to_go_and_back_fragment);
                    }else{
                        Navigation.findNavController(v).navigate(R.id.action_order_fragment_to_detail_fragment);
                    }
                });
        mBinding.bottomSheets.privatizeSwitchView.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    Order order = orderViewModel.getOrder();
                    if(order == null){
                        order = new Order();
                    }
                    order.setPrivatized(isChecked);
                    orderViewModel.setOrderLiveData(order);
                });
        mBinding.bottomSheets.buttonRefreshCar.setOnClickListener(
                v -> {
                    mBinding.bottomSheets.setIsLoadingCar(true);
                    mBinding.bottomSheets.setShowErrorLoaderCar(false);
                    orderViewModel.loadCars();
                });
        mBinding.bottomSheets.placeCountTextView.setOnClickListener(
                v -> {
                    NavHostFragment.findNavController(this).navigate(R.id.action_order_fragment_to_place_fragment);
                });
    }

    /*
    private void expandOrderDetails() {
        if(sheetBehavior != null && mOrigin != null && mDestination != null){
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
     */

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
                                        //orderViewModel.setOrigin(getString(R.string.my_location), latLng, true);
                                    }else{
                                        //orderViewModel.setReturn(getString(R.string.my_location), latLng, true);
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

}

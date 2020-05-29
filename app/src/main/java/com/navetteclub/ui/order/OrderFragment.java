package com.navetteclub.ui.order;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateUtils;
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
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.navetteclub.BuildConfig;
import com.navetteclub.R;
import com.navetteclub.api.models.google.Leg;
import com.navetteclub.api.models.google.Route;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.database.entity.Car;
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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;

import static com.navetteclub.ui.order.OrderType.*;


public class OrderFragment extends Fragment implements OnMapReadyCallback, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

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

    private Polyline lineGo;

    private Polyline lineBack;

    private Marker item1Marker;

    private Marker clubMarker;

    private Marker item2Marker;

    private BottomSheetBehavior sheetBehavior;

    private CarRecyclerViewAdapter mAdapter;

    private Calendar calendar;

    private OnClickItemListener<CarAndModel> mListerner = new OnClickItemListener<CarAndModel>() {
        @Override
        public void onClick(View v, int position, CarAndModel item) {
            orderViewModel.setCarLiveData(item.getCar());
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

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getChildFragmentManager().findFragmentByTag("Datepickerdialog");
        TimePickerDialog tpd = (TimePickerDialog) getChildFragmentManager().findFragmentByTag("TimepickerDialog");
        if(tpd != null) tpd.setOnTimeSetListener(this);
        if(dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        Log.d(TAG, date);
        if(calendar==null){
            calendar = Calendar.getInstance();
        }
        calendar.set(year, monthOfYear, dayOfMonth);

        //dateTextView.setText(date);
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd =TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.dismissOnPause(true);
        tpd.setVersion(TimePickerDialog.Version.VERSION_2);
        tpd.show(getChildFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = "You picked the following time: "+hourOfDay+"h"+minute+"m"+second;
        Log.d(TAG, time);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        Item item = orderViewModel.getItem1();
        if(item==null){
            item = new Item();
        }
        item.setRideAt(calendar.getTime());
        orderViewModel.setItem1LiveData(item);
    }

    private String getDateString(Date date) {
        if (date == null) return getString(R.string.now);
        long now = System.currentTimeMillis();
        CharSequence dateStr = DateUtils.getRelativeTimeSpanString(date.getTime(), now, DateUtils.MINUTE_IN_MILLIS);
        return (String) dateStr;
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
        googleViewModel = new ViewModelProvider(requireActivity(), factory).get(GoogleViewModel.class);

        /* Course d'aller */
        googleViewModel.getDirection1Result().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }
                    mBinding.bottomSheets.setIsLoadingDirection(false);
                    mBinding.bottomSheets.setShowErrorLoaderDirection(false);
                    if(result.body()!=null){
                        for (int i = 0; i < result.body().getRoutes().size(); i++) {
                            Route route = result.body().getRoutes().get(i);
                            String direction = route.getOverviewPolyline().getPoints();
                            for(Leg leg: route.getLegs()){
                                Item item = orderViewModel.getItem1();
                                if(item==null){
                                    item = new Item();
                                }
                                item.setDuration(leg.getDuration().getText());
                                item.setDurationValue(leg.getDuration().getValue());
                                item.setDistance(leg.getDistance().getText());
                                item.setDistanceValue(leg.getDistance().getValue());
                                item.setDirection(direction);
                                orderViewModel.setItem1LiveData(item);
                            }
                        }
                    }
                });
        googleViewModel.getError1Result().observe(getViewLifecycleOwner(),
                error -> {
                    if(error==null){
                        return;
                    }
                    mBinding.bottomSheets.setIsLoadingDirection(false);
                    mBinding.bottomSheets.setShowErrorLoaderDirection(true);
                });

        /* Course de retours */
        googleViewModel.getDirection2Result().observe(getViewLifecycleOwner(),
                result -> {
                    if(result==null){
                        return;
                    }
                    mBinding.bottomSheets.setIsLoadingDirection(false);
                    mBinding.bottomSheets.setShowErrorLoaderDirection(false);
                    if(result.body()!=null){
                        for (int i = 0; i < result.body().getRoutes().size(); i++) {
                            Route route = result.body().getRoutes().get(i);
                            String direction = route.getOverviewPolyline().getPoints();
                            for(Leg leg: route.getLegs()){
                                Item item = orderViewModel.getItem2();
                                if(item==null){
                                    item = new Item();
                                }
                                item.setDuration(leg.getDuration().getText());
                                item.setDurationValue(leg.getDuration().getValue());
                                item.setDistance(leg.getDistance().getText());
                                item.setDistanceValue(leg.getDistance().getValue());
                                item.setDirection(direction);
                                orderViewModel.setItem2LiveData(item);
                            }
                        }
                    }
                });
        googleViewModel.getError2Result().observe(getViewLifecycleOwner(),
                error -> {
                    if(error==null){
                        return;
                    }
                    mBinding.bottomSheets.setIsLoadingDirection(false);
                    mBinding.bottomSheets.setShowErrorLoaderDirection(true);
                });
    }

    private void setupOrderViewModel() {
        MyViewModelFactory factory = MyViewModelFactory.getInstance(requireActivity().getApplication());
        orderViewModel = new ViewModelProvider(this, factory).get(OrderViewModel.class);
        orderViewModel.getOriginLiveData().observe(getViewLifecycleOwner(), value -> mBinding.setOrigin(value));
        orderViewModel.getDestinationLiveData().observe(getViewLifecycleOwner(), value -> mBinding.setDestination(value));
        orderViewModel.getBackLiveData().observe(getViewLifecycleOwner(), value -> mBinding.setBack(value));
        orderViewModel.getItem1PointLiveData().observe(getViewLifecycleOwner(),
                point -> {
                    validate();
                    Point origin = orderViewModel.getOriginPoint();
                    Point destination = orderViewModel.getDestinationPoint();
                    if(origin!=null && destination!=null){
                        loadDirection(googleViewModel, origin.toLatLng(), destination.toLatLng(), true);
                    }
                    if(point!=null){
                        item1Marker = drawItemMarker(item1Marker, point);
                    }
                });
        orderViewModel.getClubPointLiveData().observe(getViewLifecycleOwner(),
                point -> {
                    validate();
                    expandOrderDetails();
                    Point origin = orderViewModel.getOriginPoint();
                    Point destination = orderViewModel.getDestinationPoint();
                    Point back = orderViewModel.getBackPoint();
                    if(origin!=null && destination!=null){
                        loadDirection(googleViewModel, origin.toLatLng(), destination.toLatLng(), true);
                    }
                    if(back!=null && destination!=null){
                        loadDirection(googleViewModel, destination.toLatLng(), back.toLatLng(), false);
                    }
                    Club club = orderViewModel.getClub();
                    if(point!=null && club!=null){
                        drawClubMarker(point, club);
                    }
                });
        orderViewModel.getItem2PointLiveData().observe(getViewLifecycleOwner(),
                point -> {
                    validate();
                    Point destination = orderViewModel.getDestinationPoint();
                    Point back = orderViewModel.getBackPoint();
                    if(back!=null && destination!=null){
                        loadDirection(googleViewModel, destination.toLatLng(), back.toLatLng(), false);
                    }
                    if(point!=null){
                        item2Marker = drawItemMarker(item2Marker, point);
                    }
                });
        orderViewModel.getItem1LiveData().observe(getViewLifecycleOwner(),
                item -> {
                    if(item!=null){
                        validate();
                        loadCart();
                        expandOrderDetails();
                        mBinding.bottomSheets.setDuration(item.getDuration());
                        mBinding.bottomSheets.setDistance(item.getDistance());
                        mBinding.bottomSheets.setOrderDate(getDateString(item.getRideAt()));
                        String direction = item.getDirection();
                        if(direction!=null && mMap!=null) {
                            List<LatLng> points = Utils.decodePoly(direction);
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
                    if(item!=null){
                        validate();
                        loadCart();
                        String direction = item.getDirection();
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
                    if(club!=null){
                        validate();
                        loadCart();
                        mBinding.bottomSheets.setIsLoadingCar(true);
                        mBinding.bottomSheets.setShowErrorLoaderCar(false);
                        orderViewModel.loadCars(club);
                    }
                    Point point = orderViewModel.getClubPoint();
                    if(point!=null && club!=null){
                        drawClubMarker(point, club);
                    }
                });
        orderViewModel.getCarLiveData().observe(getViewLifecycleOwner(),
                car -> {
                    if(car!=null){
                        validate();
                        Order order = orderViewModel.getOrder();
                        if(order!=null && order.getPrivatized()){
                            order.setPlace(car.getPlace());
                            orderViewModel.setOrderLiveData(order);
                            orderViewModel.setPlaceLiveData(car.getPlace());
                        }
                    }
                });
        orderViewModel.getPlaceLiveData().observe(getViewLifecycleOwner(), place -> {
            validate();
            mBinding.bottomSheets.setPlace(place);
            loadCart();
        });
        orderViewModel.getCartResult().observe(getViewLifecycleOwner(),
                cart -> {
                    if(cart!=null) {
                        mBinding.bottomSheets.setIsLoadingCart(false);
                        if(cart.getError()!=null){
                            mBinding.bottomSheets.setErrorLoaderCart(getString(cart.getError()));
                            mBinding.bottomSheets.setShowErrorLoaderCart(true);
                        }
                        if(cart.getSuccess()!=null){
                            OrderWithDatas data = cart.getSuccess();
                            if(data!=null){
                                Order order = data.getOrder();
                                if(order!=null) {
                                    Order orderLive = orderViewModel.getOrder();
                                    orderLive.setCoefficient(order.getCoefficient());
                                    orderLive.setSubtotal(order.getSubtotal());
                                    orderLive.setTotal(order.getTotal());
                                    orderLive.setAmount(order.getAmount());
                                    orderViewModel.setOrderLiveData(orderLive);
                                    mBinding.setAmount(order.getTotalStr());
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
                        mBinding.bottomSheets.setShowErrorLoaderCar(true);
                    }
                    if(result.getSuccess()!=null){
                        ArrayList<CarAndModel> items = (ArrayList<CarAndModel>) result.getSuccess();
                        if(items.isEmpty()){
                            mBinding.bottomSheets.setShowErrorLoaderCar(true);
                        }else{
                            mBinding.bottomSheets.setShowErrorLoaderCar(false);
                            mAdapter.setItems(items);
                            orderViewModel.setCarLiveData(items.get(0).getCar());
                            mAdapter.setSelected(0);
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
                            mBinding.bottomSheets.setAmount(order.getTotalStr());
                        }
                    }
                });
    }

    private void validate() {
        mBinding.bottomSheets.setIsOrderValid(orderViewModel.isValid());
    }

    private void loadCart() {
        Club club = orderViewModel.getClub();
        if(club==null) return;
        Item item = orderViewModel.getItem1();
        if(item==null) return;
        Call<RetrofitResponse<OrderWithDatas>> cartRetrofitRequest = orderViewModel.getCart();
        mBinding.bottomSheets.setIsLoadingCart(true);
        mBinding.bottomSheets.setShowErrorLoaderCart(false);
    }

    private void loadDirection(GoogleViewModel googleViewModel, LatLng origin, LatLng destination, boolean isMain) {
        if( (origin == null) || (destination == null)){
            return;
        }
        mBinding.bottomSheets.setIsLoadingDirection(true);
        mBinding.bottomSheets.setShowErrorLoaderDirection(false);
        googleViewModel.loadDirection(getString(R.string.google_maps_key), origin, destination, null, isMain);
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
                    .load(BuildConfig.BASE_URL + club.getImageUrl())
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
        mBinding.bottomSheets.setAmount("...");
        mBinding.bottomSheets.setDuration("...");
        mBinding.bottomSheets.setDistance("...");
        mBinding.swapPoints.setOnClickListener(v -> orderViewModel.swap());
        mBinding.originText.setOnClickListener(
                v -> {
                    OrderType orderType = orderViewModel.getOrderType();
                    if (orderType == BACK) {
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
                    if (orderType == BACK) {
                        OrderFragmentDirections.ActionOrderToSearch action = OrderFragmentDirections.actionOrderToSearch();
                        action.setSearchType(SearchType.RETOURS);
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
                    orderViewModel.setOrderTypeLiveData(GO);
                    orderViewModel.setItem2LiveData((Item) null);
                    orderViewModel.setItem2PointLiveData(null);
                });
        mBinding.bottomSheets.bookNowButton.setOnClickListener(
                v -> {
                    if(orderViewModel.getOrderType() == GO){
                        Navigation.findNavController(v).navigate(R.id.action_order_fragment_to_go_and_back_fragment);
                    }else{
                        Navigation.findNavController(v).navigate(R.id.action_order_fragment_to_cart_fragment);
                    }
                });
        mBinding.bottomSheets.privatizeSwitchView.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    Order order = orderViewModel.getOrder();
                    if(order == null){
                        order = new Order();
                    }
                    order.setPrivatized(isChecked);

                    Car car = orderViewModel.getCar();
                    if(car!=null){
                        order.setPlace(car.getPlace());
                        orderViewModel.setOrderLiveData(order);
                        orderViewModel.setPlaceLiveData(car.getPlace());
                    }else{
                        orderViewModel.setOrderLiveData(order);
                    }
                    mBinding.bottomSheets.setIsPrivatized(isChecked);
                });
        mBinding.bottomSheets.priceError.setOnClickListener(
                v -> {
                    loadCart();
                });
        mBinding.bottomSheets.delayError.setOnClickListener(
                v -> {
                    reloadDirection();
                });
        mBinding.bottomSheets.distanceError.setOnClickListener(
                v -> {
                    reloadDirection();
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

        mBinding.bottomSheets.dateTextView.setOnClickListener(v -> openDateTimePicker());

        mBinding.bottomSheets.dateImageView.setOnClickListener(
                v -> {
                    Item item = orderViewModel.getItem1();
                    if(item!=null && item.getRideAt()!=null){
                        item.setRideAt(null);
                        orderViewModel.setItem1LiveData(item);
                    }else if(item!=null){
                        openDateTimePicker();
                    }
                });

    }

    private void openDateTimePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                OrderFragment.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        Calendar calendar = Calendar.getInstance();
        dpd.setMinDate(calendar);
        calendar.roll(Calendar.MONTH, 2);
        dpd.setMaxDate(calendar);
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.dismissOnPause(true);
        dpd.show(getChildFragmentManager(), "Datepickerdialog");
    }

    private void reloadDirection() {
        Point origin = orderViewModel.getOriginPoint();
        Point destination = orderViewModel.getDestinationPoint();
        Point back = orderViewModel.getBackPoint();
        if(origin!=null && destination!=null){
            loadDirection(googleViewModel, origin.toLatLng(), destination.toLatLng(), true);
        }
        if(back!=null && destination!=null){
            loadDirection(googleViewModel, destination.toLatLng(), back.toLatLng(), false);
        }
    }

    private void expandOrderDetails() {
        Club club = orderViewModel.getClub();
        Item item1 = orderViewModel.getItem1();
        if(sheetBehavior != null && club != null && item1 != null){
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
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.MAP_ZOOM));
                                }else{
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.DEFAULT_LOCATION, Constants.MAP_ZOOM));
                                }
                            } else {
                                Log.e(TAG, "Exception: %s", task.getException());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.DEFAULT_LOCATION, Constants.MAP_ZOOM));
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

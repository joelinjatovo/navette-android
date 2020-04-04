package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.OrderRequest;
import com.navetteclub.api.models.google.Distance;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.ClubApiService;
import com.navetteclub.api.services.OrderApiService;
import com.navetteclub.database.callback.UpsertCallback;
import com.navetteclub.database.entity.Car;
import com.navetteclub.database.entity.CarAndModel;
import com.navetteclub.database.entity.Club;
import com.navetteclub.database.entity.ClubAndPoint;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.OrderWithPoints;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.repositories.CarRepository;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.Log;

import java.io.CharArrayReader;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderViewModel extends ViewModel implements UpsertCallback<CarAndModel> {

    private static final String TAG = OrderViewModel.class.getSimpleName();

    private OrderWithDatas orderWithDatas;

    private MutableLiveData<OrderWithDatas> orderWithDatasLiveData = new MutableLiveData<>();

    private MutableLiveData<List<CarAndModel>> cars = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<List<CarAndModel>>> carsResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<OrderWithDatas>> orderResult = new MutableLiveData<>();

    private CarRepository carRepository;

    public OrderViewModel(CarRepository carRepository) {
        this.carRepository = carRepository;
        this.setPlace(1);
    }

    private void _init(){
        if(orderWithDatas==null)
            orderWithDatas = new OrderWithDatas();

        List<Point> points = orderWithDatas.getPoints();
        if(points==null){
            points = new ArrayList<>(3);
            points.add(null);
            points.add(null);
            points.add(null);
        }
        orderWithDatas.setPoints(points);

        Order order = orderWithDatas.getOrder();
        if(order==null){
            order = new Order();
        }
        orderWithDatas.setOrder(order);
    }

    public MutableLiveData<List<CarAndModel>> getCars() {
        return cars;
    }

    public MutableLiveData<RemoteLoaderResult<List<CarAndModel>>> getCarsResult() {
        return carsResult;
    }

    public void setOrigin(String name, LatLng latLng) {
        _init();
        List<Point> points = orderWithDatas.getPoints();
        Point point = points.get(0);
        if(point==null){
            point = new Point();
        }

        point.setName(name);

        if(latLng!=null){
            point.setLat(latLng.latitude);
            point.setLng(latLng.longitude);
        }

        points.set(0, point);

        orderWithDatas.setPoints(points);

        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void setOrigin(Place place) {
        setOrigin(place.getName(), place.getLatLng());
    }

    private void setDestination(String name, LatLng latLng) {
        _init();
        List<Point> points = orderWithDatas.getPoints();
        Point point = points.get(1);
        if(point==null){
            point = new Point();
        }

        point.setName(name);

        if(latLng!=null){
            point.setLat(latLng.latitude);
            point.setLng(latLng.longitude);
        }

        points.set(1, point);

        orderWithDatas.setPoints(points);

    }

    public void setReturn(String name, LatLng latLng) {
        _init();
        List<Point> points = orderWithDatas.getPoints();
        Point point = points.get(2);
        if(point==null){
            point = new Point();
        }

        point.setName(name);

        if(latLng!=null){
            point.setLat(latLng.latitude);
            point.setLng(latLng.longitude);
        }

        points.set(2, point);

        orderWithDatas.setPoints(points);

        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void setReturn(Place place) {
        if(place==null){
            _init();
            List<Point> points = orderWithDatas.getPoints();
            points.set(2, null);
            orderWithDatas.setPoints(points);
            orderWithDatasLiveData.setValue(orderWithDatas);
        }else {
            setReturn(place.getName(), place.getLatLng());
        }
    }

    public void setPlace(int place) {
        _init();
        Order order = orderWithDatas.getOrder();
        order.setPlace(place);
        orderWithDatas.setOrder(order);
        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void setPrivatized(boolean privatized) {
        _init();
        Order order = orderWithDatas.getOrder();
        order.setPrivatized(privatized);
        orderWithDatas.setOrder(order);
        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void setCar(Car car) {
        _init();
        orderWithDatas.setCar(car);
        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void setClub(Club club, Point point) {
        _init();
        orderWithDatas.setClub(club);
        LatLng latLng = new LatLng(
            point.getLat(),
            point.getLng()
        );
        setDestination(club.getName(), latLng);
        orderWithDatasLiveData.setValue(orderWithDatas);

        loadCars(club);
    }

    public void setDistance(String distance) {
        _init();
        Order order = orderWithDatas.getOrder();
        order.setDistance(distance);
        orderWithDatas.setOrder(order);
        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void setDelay(String delay) {
        _init();
        Order order = orderWithDatas.getOrder();
        order.setDelay(delay);
        orderWithDatas.setOrder(order);
        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void setDirection(String direction) {
        _init();
        Order order = orderWithDatas.getOrder();
        order.setDirection(direction);
        orderWithDatas.setOrder(order);
        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void setOrder(OrderWithDatas order) {
        orderWithDatas = order;
        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void loadCars(Club club){
        Log.d(TAG, "ClubApiService.getCars(" + club.getId() +")");
        ClubApiService service = RetrofitClient.getInstance().create(ClubApiService.class);
        Call<RetrofitResponse<List<CarAndModel>>> call = service.getCars(club.getId());
        call.enqueue(new Callback<RetrofitResponse<List<CarAndModel>>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<List<CarAndModel>>> call,
                                   @NonNull Response<RetrofitResponse<List<CarAndModel>>> response) {
                Log.e(TAG, response.toString());
                if (response.body() != null) {
                    Log.e(TAG, response.body().toString());
                    CarAndModel[] items = new CarAndModel[response.body().getData().size()];
                    for(int i = 0 ; i < response.body().getData().size(); i++){
                        items[i] = response.body().getData().get(i);
                    }

                    carRepository.upsert(orderWithDatas.getClub(), OrderViewModel.this, items);

                    carsResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                }else{
                    carsResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<List<CarAndModel>>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
                carsResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void placeOrder() {
        Club club = orderWithDatas.getClub();

        Log.d(TAG, "OrderApiService.placeOrder()");
        OrderApiService service = RetrofitClient.getInstance().create(OrderApiService.class);
        Call<RetrofitResponse<OrderWithDatas>> call = service.createOrder(club.getId(), new OrderRequest(orderWithDatas));
        call.enqueue(new Callback<RetrofitResponse<OrderWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<OrderWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<OrderWithDatas>> response) {
                Log.e(TAG, response.toString());
                if (response.body() != null){
                    Log.e(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        orderResult.setValue(new RemoteLoaderResult<OrderWithDatas>(response.body().getData()));
                    }else{
                        orderResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
                    }
                } else {
                    orderResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<OrderWithDatas>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
                orderResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void pay(String paymentType) {
        Order order = orderWithDatas.getOrder();

        Log.d(TAG, "service.pay(" + paymentType + ")");
        OrderApiService service = RetrofitClient.getInstance().create(OrderApiService.class);
        Call<RetrofitResponse<OrderWithDatas>> call = service.confirmPayment(order.getRid(), paymentType);
        call.enqueue(new Callback<RetrofitResponse<OrderWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<OrderWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<OrderWithDatas>> response) {
                Log.e(TAG, response.toString());
                if (response.body() != null){
                    Log.e(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        orderResult.setValue(new RemoteLoaderResult<OrderWithDatas>(response.body().getData()));
                    }else{
                        orderResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
                    }
                } else {
                    orderResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<OrderWithDatas>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
                orderResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public OrderWithDatas getOrderWithDatas() {
        return orderWithDatas;
    }

    public void setOrderWithDatasLiveData(OrderWithDatas orderWithDatas) {
        this.orderWithDatas = orderWithDatas;
        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public MutableLiveData<OrderWithDatas> getOrderWithDatasLiveData() {
        return orderWithDatasLiveData;
    }

    public MutableLiveData<RemoteLoaderResult<OrderWithDatas>> getOrderResult() {
        return orderResult;
    }

    public void setOrderResult(RemoteLoaderResult<OrderWithDatas> data) {
        orderResult.setValue(data);
    }

    @Override
    public void onUpsertError() {

    }

    @Override
    public void onUpsertSuccess(List<CarAndModel> items) {
        cars.setValue(items);
    }
}

package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.OrderParam;
import com.navetteclub.api.models.OrderRequest;
import com.navetteclub.api.models.stripe.MyPaymentIntent;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.CashApiService;
import com.navetteclub.api.services.ClubApiService;
import com.navetteclub.api.services.OrderApiService;
import com.navetteclub.api.services.StripeApiService;
import com.navetteclub.database.callback.UpsertCallback;
import com.navetteclub.database.entity.Car;
import com.navetteclub.database.entity.CarAndModel;
import com.navetteclub.database.entity.Club;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.entity.User;
import com.navetteclub.database.repositories.CarRepository;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderViewModel extends ViewModel {

    private static final String TAG = OrderViewModel.class.getSimpleName();

    private OrderWithDatas orderWithDatas;

    private MutableLiveData<Point> origin = new MutableLiveData<>();

    private MutableLiveData<Point> destination = new MutableLiveData<>();

    private MutableLiveData<Point> retours = new MutableLiveData<>();

    private MutableLiveData<OrderWithDatas> orderWithDatasLiveData = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<List<CarAndModel>>> carsResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<OrderWithDatas>> orderResult = new MutableLiveData<>();

    public OrderViewModel(CarRepository carRepository) {
        Log.d(TAG, "new  OrderViewModel() + constructor");
        this.setPlace(1);
    }

    public void refresh() {
        orderWithDatas = null;
        origin = new MutableLiveData<>();
        destination = new MutableLiveData<>();
        retours = new MutableLiveData<>();
        orderWithDatasLiveData = new MutableLiveData<>();
        carsResult = new MutableLiveData<>();
        orderResult = new MutableLiveData<>();
    }

    private void mayBeInit(){
        if(orderWithDatas==null)
            orderWithDatas = new OrderWithDatas();

        Order order = orderWithDatas.getOrder();
        if(order==null){
            order = new Order();
        }
        orderWithDatas.setOrder(order);
    }


    public void setOrigin(Point point, boolean notify) {
        Log.d(TAG, "setOrigin(point, notify)");

        origin.setValue(point);

        if(notify){
            mayBeInit();
            orderWithDatas.setOrigin(point);
            orderWithDatasLiveData.setValue(orderWithDatas);
        }
    }

    public void setOrigin(String name, LatLng latLng, boolean notify) {
        Point point = new Point(name, latLng);
        setOrigin(point, notify);
    }

    public void setOrigin(@NonNull Place place, boolean notify) {
        setOrigin(place.getName(), place.getLatLng(), notify);
    }

    public MutableLiveData<Point> getOrigin() {
        return origin;
    }

    public void setDestination(Point point, boolean notify) {
        destination.setValue(point);
        if(notify) {
            mayBeInit();
            orderWithDatas.setDestination(point);
            orderWithDatasLiveData.setValue(orderWithDatas);
        }
    }

    private void setDestination(String name, LatLng latLng, boolean notify) {
        Point point = new Point(name, latLng);
        setDestination(point, notify);
    }

    public MutableLiveData<Point> getDestination() {
        return destination;
    }

    public void setReturn(@Nullable Point point, boolean notify) {
        retours.setValue(point);
        if(notify) {
            mayBeInit();
            orderWithDatas.setRetours(point);
            orderWithDatasLiveData.setValue(orderWithDatas);
        }
    }

    public void setReturn(String name, LatLng latLng, boolean notify) {
        Point point = new Point(name, latLng);
        setReturn(point, notify);
    }

    public void setReturn(@NonNull Place place, boolean notify) {
        setReturn(place.getName(), place.getLatLng(), notify);
    }

    public MutableLiveData<Point> getRetours() {
        return retours;
    }

    public void setPlace(int place) {
        mayBeInit();
        Order order = orderWithDatas.getOrder();
        order.setPlace(place);
        orderWithDatas.setOrder(order);
        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void setPrivatized(boolean privatized) {
        mayBeInit();
        Order order = orderWithDatas.getOrder();
        order.setPrivatized(privatized);
        orderWithDatas.setOrder(order);
        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void setCar(Car car) {
        mayBeInit();
        orderWithDatas.setCar(car);
        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void setClub(Club club, Point point) {
        mayBeInit();
        orderWithDatas.setClub(club);
        point.setName(club.getName());
        setDestination(point, true);
        loadCars(club);
    }

    public void setDistance(String distance) {
        mayBeInit();
        Order order = orderWithDatas.getOrder();
        order.setDistance(distance);
        orderWithDatas.setOrder(order);
        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void setDelay(String delay) {
        mayBeInit();
        Order order = orderWithDatas.getOrder();
        order.setDelay(delay);
        orderWithDatas.setOrder(order);
        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void setDirection(String direction) {
        mayBeInit();
        Order order = orderWithDatas.getOrder();
        order.setDirection(direction);
        orderWithDatas.setOrder(order);
        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void setOrder(OrderWithDatas order) {
        Log.d(TAG, "setOrder(" + order + ")");
        if(order!=null){
            setOrigin(order.getOrigin(), false);
            setDestination(order.getDestination(), false);
            setReturn(order.getRetours(), false);
        }else{
            setOrigin((Point) null, false);
            setDestination((Point) null, false);
            setReturn((Point) null, false);
        }
        orderWithDatas = order;
        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public OrderWithDatas getOrder() {
        return orderWithDatas;
    }

    public MutableLiveData<OrderWithDatas> getOrderLiveData() {
        return orderWithDatasLiveData;
    }

    public void setOrderResult(RemoteLoaderResult<OrderWithDatas> data) {
        orderResult.setValue(data);
    }

    public MutableLiveData<RemoteLoaderResult<OrderWithDatas>> getOrderResult() {
        return orderResult;
    }

    public MutableLiveData<RemoteLoaderResult<List<CarAndModel>>> getCarsResult(){
        return carsResult;
    }

    public boolean loadCars(){
        if(orderWithDatas!=null){
            loadCars(orderWithDatas.getClub());
            return true;
        }
        return false;
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

    public void placeOrder(User user) {
        Club club = orderWithDatas.getClub();

        Log.d(TAG, "OrderApiService.placeOrder()");
        OrderApiService service = RetrofitClient.getInstance().create(OrderApiService.class);
        Call<RetrofitResponse<OrderWithDatas>> call = service.createOrder(user.getAuthorizationToken(), club.getId(), new OrderRequest(orderWithDatas));
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

    public void payPerCash(User user) {
        Order order = orderWithDatas.getOrder();

        Log.d(TAG, "service.payPerCash()");
        CashApiService service = RetrofitClient.getInstance().create(CashApiService.class);
        Call<RetrofitResponse<OrderWithDatas>> call = service.pay(user.getAuthorizationToken(), new OrderParam(order));
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
}

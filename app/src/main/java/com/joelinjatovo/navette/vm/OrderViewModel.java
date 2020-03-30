package com.joelinjatovo.navette.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.api.clients.RetrofitClient;
import com.joelinjatovo.navette.api.models.OrderRequest;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.api.services.ClubApiService;
import com.joelinjatovo.navette.api.services.OrderApiService;
import com.joelinjatovo.navette.database.callback.UpsertCallback;
import com.joelinjatovo.navette.database.entity.Car;
import com.joelinjatovo.navette.database.entity.CarAndModel;
import com.joelinjatovo.navette.database.entity.Club;
import com.joelinjatovo.navette.database.entity.ClubAndPoint;
import com.joelinjatovo.navette.database.entity.Order;
import com.joelinjatovo.navette.database.entity.OrderWithDatas;
import com.joelinjatovo.navette.database.entity.OrderWithPoints;
import com.joelinjatovo.navette.database.entity.Point;
import com.joelinjatovo.navette.database.repositories.CarRepository;
import com.joelinjatovo.navette.models.RemoteLoaderResult;
import com.joelinjatovo.navette.utils.Log;

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

    private MutableLiveData<RemoteLoaderResult<List<CarAndModel>>> retrofitResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<OrderWithDatas>> orderResult = new MutableLiveData<>();

    private CarRepository carRepository;

    public OrderViewModel(CarRepository carRepository) {
        this.carRepository = carRepository;
        this.setPlace(1);
    }

    public MutableLiveData<List<CarAndModel>> getCars() {
        return cars;
    }

    public MutableLiveData<RemoteLoaderResult<List<CarAndModel>>> getRetrofitResult() {
        return retrofitResult;
    }

    public void setOrigin(String name, LatLng latLng) {
        if(orderWithDatas==null)
            orderWithDatas = new OrderWithDatas();

        List<Point> points = orderWithDatas.getPoints();
        if(points==null){
            points = new ArrayList<>(3);
            points.add(null);
            points.add(null);
            points.add(null);
        }

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
        if(orderWithDatas==null)
            orderWithDatas = new OrderWithDatas();

        List<Point> points = orderWithDatas.getPoints();
        if(points==null){
            points = new ArrayList<>(3);
            points.add(null);
            points.add(null);
            points.add(null);
        }

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
        if(orderWithDatas==null)
            orderWithDatas = new OrderWithDatas();

        List<Point> points = orderWithDatas.getPoints();
        if(points==null){
            points = new ArrayList<>(3);
            points.add(null);
            points.add(null);
            points.add(null);
        }

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

    public void setPlace(int place) {
        if(orderWithDatas==null)
            orderWithDatas = new OrderWithDatas();

        Order order = orderWithDatas.getOrder();
        if(order==null){
            order = new Order();
        }
        order.setPlace(place);
        orderWithDatas.setOrder(order);

        orderWithDatasLiveData.setValue(orderWithDatas);

    }

    public void setCar(Car car) {
        if(orderWithDatas==null)
            orderWithDatas = new OrderWithDatas();
        orderWithDatas.setCar(car);

        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void setClub(Club club, Point point) {
        if(orderWithDatas==null)
            orderWithDatas = new OrderWithDatas();
        orderWithDatas.setClub(club);

        LatLng latLng = new LatLng(
            point.getLat(),
            point.getLng()
        );
        setDestination(club.getName(), latLng);

        loadCars(club);

        orderWithDatasLiveData.setValue(orderWithDatas);
    }

    public void loadCars(Club club){
        Log.d(TAG, "service.getCars()");
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

                    retrofitResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                }else{
                    retrofitResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<List<CarAndModel>>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.getMessage(), throwable);
                retrofitResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void placeOrder() {
        Log.d(TAG, "service.placeOrder()");
        OrderApiService service = RetrofitClient.getInstance().create(OrderApiService.class);
        Call<RetrofitResponse<OrderWithDatas>> call = service.createOrder(new OrderRequest(orderWithDatas));
        call.enqueue(new Callback<RetrofitResponse<OrderWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<OrderWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<OrderWithDatas>> response) {
                Log.e(TAG, response.toString());
                if (response.body() != null && response.body().isSuccess()) {
                    Log.e(TAG, response.body().toString());
                    orderResult.setValue(new RemoteLoaderResult<OrderWithDatas>(response.body().getData()));
                }else{
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

    public MutableLiveData<OrderWithDatas> getOrderWithDatasLiveData() {
        return orderWithDatasLiveData;
    }

    public MutableLiveData<RemoteLoaderResult<OrderWithDatas>> getOrderResult() {
        return orderResult;
    }

    @Override
    public void onUpsertError() {

    }

    @Override
    public void onUpsertSuccess(List<CarAndModel> items) {
        cars.setValue(items);
    }
}

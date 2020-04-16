package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.OrderParam;
import com.navetteclub.api.models.OrderRequest;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.CashApiService;
import com.navetteclub.api.services.ClubApiService;
import com.navetteclub.api.services.OrderApiService;
import com.navetteclub.database.entity.Car;
import com.navetteclub.database.entity.CarAndModel;
import com.navetteclub.database.entity.Club;
import com.navetteclub.database.entity.Item;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.entity.ItemWithDatas;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.ui.order.OrderType;
import com.navetteclub.utils.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderViewModel extends ViewModel {

    private static final String TAG = OrderViewModel.class.getSimpleName();

    private OrderType orderType;

    private MutableLiveData<OrderType> orderTypeLiveData = new MutableLiveData<>();

    private Club club;

    private MutableLiveData<Club> clubLiveData = new MutableLiveData<>();

    private Point clubPoint;

    private MutableLiveData<Point> clubPointLiveData = new MutableLiveData<>();

    private Order order;

    private MutableLiveData<Order> orderLiveData = new MutableLiveData<>();

    private Car car;

    private MutableLiveData<Car> carLiveData = new MutableLiveData<>();

    private Item item1;

    private MutableLiveData<Item> item1LiveData = new MutableLiveData<>();

    private Point item1Point;

    private MutableLiveData<Point> item1PointLiveData = new MutableLiveData<>();

    private Item item2;

    private MutableLiveData<Item> item2LiveData = new MutableLiveData<>();

    private Point item2Point;

    private MutableLiveData<Point> item2PointLiveData = new MutableLiveData<>();

    private List<ItemWithDatas> items;

    private MutableLiveData<List<ItemWithDatas>> itemsLiveData = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<List<CarAndModel>>> carsResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<OrderWithDatas>> orderResult = new MutableLiveData<>();

    public String origin;

    public String destination;

    public String back;

    public OrderViewModel(){
        refresh();
    }

    public void refresh(){
        setClubLiveData(null);
        setClubPointLiveData(new Point());
        setItem1LiveData(new Item(), new Point());
        setItem2LiveData(new Item(), new Point());
        setOrderLiveData(null);
        setCarLiveData(null);
        setItemsLiveData(null);
        setCarsResult(null);
        setOrderResult(null);
        setOrderTypeLiveData(OrderType.GO);
    }


    public void loadCars(){
        loadCars(club);
    }

    public void loadCars(Club club){
        if(club==null) return;
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

    public void placeOrder(String token) {
        Log.d(TAG, "OrderApiService.placeOrder()");
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrder(order).setItems(items);

        OrderApiService service = RetrofitClient.getInstance().create(OrderApiService.class);
        Call<RetrofitResponse<OrderWithDatas>> call = service.createOrder(token, club.getId(), orderRequest);
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

    public void payPerCash(String token, String orderId) {
        Log.d(TAG, "service.payPerCash()");
        OrderParam orderParam = new OrderParam(orderId);

        CashApiService service = RetrofitClient.getInstance().create(CashApiService.class);
        Call<RetrofitResponse<OrderWithDatas>> call = service.pay(token, orderParam);
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

    public LiveData<RemoteLoaderResult<List<CarAndModel>>> getCarsResult() {
        return this.carsResult;
    }

    public void setCarsResult(RemoteLoaderResult<List<CarAndModel>> value) {
        this.carsResult.setValue(value);
    }

    public LiveData<RemoteLoaderResult<OrderWithDatas>> getOrderResult() {
        return this.orderResult;
    }

    public void setOrderResult(RemoteLoaderResult<OrderWithDatas> value) {
        this.orderResult.setValue(value);
    }

    public LiveData<Order> getOrderLiveData() {
        return orderLiveData;
    }

    public void setOrderLiveData(Order order) {
        this.order = order;
        this.orderLiveData.setValue(order);
    }

    public Order getOrder() {
        return order;
    }

    public LiveData<Club> getClubLiveData() {
        return clubLiveData;
    }

    public Club getClub() {
        return this.club;
    }

    public void setClubLiveData(Club club) {
        if(club!=null){
            if(order==null){
                order = new Order();
            }
            this.order.setClubId(club.getId());
            this.setOrderLiveData(this.order);
        }
        this.club = club;
        this.clubLiveData.setValue(club);
    }

    public LiveData<Point> getClubPointLiveData() {
        return clubPointLiveData;
    }

    public void setClubPointLiveData(Point point) {
        this.clubPoint = point;
        this.clubPointLiveData.setValue(point);
    }

    public Point getClubPoint() {
        return clubPoint;
    }

    public LiveData<Car> getCarLiveData() {
        return carLiveData;
    }

    public void setCarLiveData(Car car) {
        if(car!=null){
            if(order==null){
                order = new Order();
            }
            this.order.setCarId(car.getId());
            this.setOrderLiveData(order);
        }
        this.car = car;
        this.carLiveData.setValue(car);
    }

    public Car getCar() {
        return car;
    }

    public List<ItemWithDatas> getItems() {
        return items;
    }

    public MutableLiveData<List<ItemWithDatas>> getItemsLiveData() {
        return itemsLiveData;
    }

    public void setItemsLiveData(List<ItemWithDatas> items) {
        this.items = items;
        this.itemsLiveData.setValue(items);
    }

    public boolean isTypeGo() {
        return orderType == OrderType.GO;
    }

    public boolean isTypeBack() {
        return orderType == OrderType.BACK;
    }

    public boolean isTypeGoBack() {
        return orderType == OrderType.GO_BACK;
    }

    public boolean isTypeCustom() {
        return orderType == OrderType.CUSTOM;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderTypeLiveData(OrderType orderType) {
        switch (orderType){
            case GO:
                origin = item1Point==null?null:item1Point.getName();
                destination = clubPoint==null?null:clubPoint.getName();
                back = null;
                break;
            case BACK:
                origin = clubPoint==null?null:clubPoint.getName();
                destination = item1Point==null?null:item1Point.getName();
                back = null;
                break;
            case GO_BACK:
                origin = item1Point==null?null:item1Point.getName();
                destination = clubPoint==null?null:clubPoint.getName();
                back = item2Point==null?null:item2Point.getName();
                break;
        }
        this.orderType = orderType;
        this.orderTypeLiveData.setValue(orderType);
    }

    public Item getItem1() {
        return item1;
    }

    public LiveData<Item> getItem1LiveData() {
        return item1LiveData;
    }

    public void setItem1LiveData(String name, LatLng latLng) {
        Item item = this.getItem1();
        if(item==null){
            item = new Item();
        }
        Point point = this.getItem1Point();
        if(point==null){
            point = new Point();
        }
        point.setName(name);
        if(latLng!=null){
            point.setLat(latLng.latitude);
            point.setLng(latLng.longitude);
        }
        setItem1LiveData(item, point);
    }

    public void setItem1LiveData(Place place) {
        Item item = this.getItem2();
        if(item==null){
            item = new Item();
        }
        Point point = this.getItem2Point();
        if(point==null){
            point = new Point();
        }
        if(place.getLatLng()!=null) {
            point.setName(place.getName());
            point.setLat(place.getLatLng().latitude);
            point.setLng(place.getLatLng().longitude);
        }
        setItem1LiveData(item, point);
    }

    public void setItem1LiveData(Item item, Point point) {
        ItemWithDatas data;
        if(items==null){
            items = new ArrayList<>(2);
            items.add(null);
            items.add(null);
            data = new ItemWithDatas();
        }else{
            data = items.get(0);
            if(data==null){
                data = new ItemWithDatas();
            }
        }

        data.setItem(item);
        data.setPoint(point);
        items.set(0, data);
        setItem1LiveData(item);
        setItem1PointLiveData(point);
    }

    public void setItem1LiveData(Item item1) {
        if(items==null){
            items = new ArrayList<>(2);
            //items.set(0, item1);
        }
        this.item1 = item1;
        this.item1LiveData.setValue(item1);
    }

    public Item getItem2() {
        return item2;
    }

    public MutableLiveData<Item> getItem2LiveData() {
        return item2LiveData;
    }

    public void setItem2LiveData(String name, LatLng latLng) {
        Item item = this.getItem2();
        if(item==null){
            item = new Item();
        }
        Point point = this.getItem2Point();
        if(point==null){
            point = new Point();
        }
        point.setName(name);
        if(latLng!=null){
            point.setLat(latLng.latitude);
            point.setLng(latLng.longitude);
        }
        setItem2LiveData(item, point);
    }

    public void setItem2LiveData(Place place) {
        Item item = this.getItem2();
        if(item==null){
            item = new Item();
        }
        Point point = this.getItem2Point();
        if(point==null){
            point = new Point();
        }
        if(place.getLatLng()!=null) {
            point.setName(place.getName());
            point.setLat(place.getLatLng().latitude);
            point.setLng(place.getLatLng().longitude);
        }
        setItem2LiveData(item, point);
    }

    public void setItem2LiveData(Item item, Point point) {
        ItemWithDatas data;
        if(items==null){
            items = new ArrayList<>(2);
            items.add(null);
            items.add(null);
            data = new ItemWithDatas();
        }else{
            data = items.get(1);
            if(data==null){
                data = new ItemWithDatas();
            }
        }

        data.setItem(item);
        data.setPoint(point);
        items.set(1, data);
        setItem2LiveData(item);
        setItem2PointLiveData(point);
    }

    public void setItem2LiveData(Item item2) {
        this.item2 = item2;
        this.item2LiveData.setValue(item2);
    }

    public Point getItem1Point() {
        return item1Point;
    }

    public LiveData<Point> getItem1PointLiveData() {
        return item1PointLiveData;
    }

    public void setItem1PointLiveData(Point point) {
        this.item1Point = point;
        this.item1PointLiveData.setValue(point);
    }

    public Point getItem2Point() {
        return item2Point;
    }

    public LiveData<Point> getItem2PointLiveData() {
        return item2PointLiveData;
    }

    public void setItem2PointLiveData(Point point) {
        this.item2Point = point;
        this.item2PointLiveData.setValue(point);
    }

    public void swap() {
        switch (orderType){
            case GO:
                setOrderTypeLiveData(OrderType.BACK);
                break;
            case BACK:
                setOrderTypeLiveData(OrderType.GO);
                break;
        }
    }
}

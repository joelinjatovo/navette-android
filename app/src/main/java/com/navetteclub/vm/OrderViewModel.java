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

    private Integer place;

    private MutableLiveData<Integer> placeLiveData = new MutableLiveData<>();

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

    private MutableLiveData<RemoteLoaderResult<OrderWithDatas>> paymentResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<OrderWithDatas>> cartResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<OrderWithDatas>> cancelResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<OrderWithDatas>> viewResult = new MutableLiveData<>();

    public String origin;

    private MutableLiveData<String> originLiveData = new MutableLiveData<>();

    public String destination;

    private MutableLiveData<String> destinationLiveData = new MutableLiveData<>();

    public String back;

    private MutableLiveData<String> backLiveData = new MutableLiveData<>();

    public OrderViewModel(){
        refresh();
    }

    public void refresh(){
        setClubLiveData(null); setClubPointLiveData(null);
        setItem1LiveData((Item) null); setItem1PointLiveData(null);
        setItem2LiveData((Item) null); setItem2PointLiveData(null);
        Order order = new Order();
        order.setPlace(1);
        setPlaceLiveData(1);
        setOrderLiveData(order);
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
        orderRequest.setOrder(order).setItems(items).checkElement();
        Log.d(TAG, "placeOrder(" + orderRequest + ")");

        OrderApiService service = RetrofitClient.getInstance().create(OrderApiService.class);
        Call<RetrofitResponse<OrderWithDatas>> call = service.createOrder(token, orderRequest);
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
                        orderResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
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

    public Call<RetrofitResponse<OrderWithDatas>> getCart() {
        Log.d(TAG+"Cart", "OrderApiService.getCart()");
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrder(order).setItems(items).checkElement();
        Log.d(TAG+"Cart", "getCart(" + orderRequest + ")");

        OrderApiService service = RetrofitClient.getInstance().create(OrderApiService.class);
        Call<RetrofitResponse<OrderWithDatas>> call = service.getCart(orderRequest);
        call.enqueue(new Callback<RetrofitResponse<OrderWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<OrderWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<OrderWithDatas>> response) {
                Log.e(TAG+"Cart", response.toString());
                if (response.body() != null){
                    Log.e(TAG+"Cart", response.body().toString());
                    if(response.body().isSuccess()) {
                        cartResult.setValue(new RemoteLoaderResult<OrderWithDatas>(response.body().getData()));
                    }else{
                        cartResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                } else {
                    cartResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<OrderWithDatas>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG+"Cart", throwable.getMessage(), throwable);
                cartResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });

        return call;
    }

    public Call<RetrofitResponse<OrderWithDatas>> getOrder(String token, String orderId) {
        Log.d(TAG+"Cart", "OrderApiService.getOrder(" + orderId + ")");
        OrderApiService service = RetrofitClient.getInstance().create(OrderApiService.class);
        Call<RetrofitResponse<OrderWithDatas>> call = service.getOrder(token, orderId);
        call.enqueue(new Callback<RetrofitResponse<OrderWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<OrderWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<OrderWithDatas>> response) {
                Log.e(TAG+"View", response.toString());
                if (response.body() != null){
                    Log.e(TAG+"View", response.body().toString());
                    if(response.body().isSuccess()) {
                        viewResult.setValue(new RemoteLoaderResult<OrderWithDatas>(response.body().getData()));
                    }else{
                        viewResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                } else {
                    viewResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<OrderWithDatas>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG+"View", throwable.getMessage(), throwable);
                viewResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });

        return call;
    }

    public void payPerCash(String token, String orderId) {
        Log.d(TAG+"Payment", "service.payPerCash(" + orderId +")");
        OrderParam orderParam = new OrderParam(orderId);

        CashApiService service = RetrofitClient.getInstance().create(CashApiService.class);
        Call<RetrofitResponse<OrderWithDatas>> call = service.pay(token, orderParam);
        call.enqueue(new Callback<RetrofitResponse<OrderWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<OrderWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<OrderWithDatas>> response) {
                Log.e(TAG, response.toString());
                if (response.body() != null){
                    Log.e(TAG+"Payment", response.body().toString());
                    if(response.body().isSuccess()) {
                        paymentResult.setValue(new RemoteLoaderResult<OrderWithDatas>(response.body().getData()));
                    }else{
                        paymentResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                } else {
                    paymentResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<OrderWithDatas>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG+"Payment", throwable.getMessage(), throwable);
                paymentResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
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
        Log.d(TAG, "setClubLiveData() " + club);
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
        Log.d(TAG, "setClubPointLiveData() " + point);
        this.clubPoint = point;
        this.clubPointLiveData.setValue(point);
        reloadPointStr();
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

    public LiveData<OrderType> getOrderTypeLiveData() {
        return orderTypeLiveData;
    }

    public void setOrderTypeLiveData(OrderType orderType) {
        Log.d(TAG, "setOrderTypeLiveData() " + orderType);
        Order order = getOrder();
        if(order == null){
            order = new Order();
        }
        if(orderType!=null){
            switch (orderType){
                case GO:
                    order.setType(Order.TYPE_GO);
                    break;
                case BACK:
                    order.setType(Order.TYPE_BACK);
                    break;
                case GO_BACK:
                    order.setType(Order.TYPE_GO_BACK);
                    break;
            }
        }
        this.orderType = orderType;
        this.orderTypeLiveData.setValue(orderType);
        reloadPointStr();
    }

    public Item getItem1() {
        return item1;
    }

    public LiveData<Item> getItem1LiveData() {
        return item1LiveData;
    }

    public void setItem1LiveData(String name, LatLng latLng) {
        Log.d(TAG, "setItem1LiveData() name " + name + " latLng =" + latLng);
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
        mayBeInitItems();

        ItemWithDatas data = null;
        data = items.get(0);
        if(data==null){
            data = new ItemWithDatas();
        }

        data.setItem(item);
        data.setPoint(point);
        items.set(0, data);
        setItem1LiveData(item);
        setItem1PointLiveData(point);
    }

    public void setItem1LiveData(Item item1) {
        mayBeInitItems();

        ItemWithDatas data = null;
        data = items.get(0);
        if(data==null){
            data = new ItemWithDatas();
        }

        if(item1!=null) {
            if (orderType == OrderType.BACK) {
                item1.setType(Order.TYPE_BACK);
            } else {
                item1.setType(Order.TYPE_GO);
            }
        }

        data.setItem(item1);
        items.set(0, data);

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
        Log.d(TAG, "setItem2LiveData() " + name + " " + latLng);
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
        mayBeInitItems();

        ItemWithDatas data = null;
        data = items.get(1);
        if(data==null){
            data = new ItemWithDatas();
        }

        data.setItem(item);
        data.setPoint(point);
        items.set(1, data);

        setItem2LiveData(item);
        setItem2PointLiveData(point);
    }

    public void setItem2LiveData(Item item2) {
        Log.d(TAG, "setItem2LiveData(Item) " + item2);
        mayBeInitItems();

        ItemWithDatas data = null;
        data = items.get(1);
        if(data==null){
            data = new ItemWithDatas();
        }

        if(item2!=null) {
            item2.setType(Order.TYPE_BACK);
        }

        data.setItem(item2);
        items.set(1, data);

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
        Log.d(TAG, "setItem1PointLiveData() [point] " + point);
        mayBeInitItems();

        ItemWithDatas data = null;
        data = items.get(0);
        if(data==null){
            data = new ItemWithDatas();
        }

        data.setPoint(point);
        items.set(0, data);

        this.item1Point = point;
        this.item1PointLiveData.setValue(point);
        reloadPointStr();
    }

    public Point getItem2Point() {
        return item2Point;
    }

    public LiveData<Point> getItem2PointLiveData() {
        return item2PointLiveData;
    }

    public void setItem2PointLiveData(Point point) {
        Log.d(TAG, "setItem2PointLiveData() " + orderType);
        mayBeInitItems();

        ItemWithDatas data = null;
        data = items.get(1);
        if(data==null){
            data = new ItemWithDatas();
        }

        data.setPoint(point);
        items.set(1, data);

        this.item2Point = point;
        this.item2PointLiveData.setValue(point);
        reloadPointStr();
    }

    public void setOriginLiveData(String value){
        this.origin = value;
        this.originLiveData.setValue(value);
    }

    public LiveData<String> getOriginLiveData(){
        return this.originLiveData;
    }

    public void setDestinationLiveData(String value){
        this.destination = value;
        this.destinationLiveData.setValue(value);
    }

    public LiveData<String> getDestinationLiveData(){
        return this.destinationLiveData;
    }

    public void setBackLiveData(String value){
        this.back = value;
        this.backLiveData.setValue(value);
    }

    public LiveData<String> getBackLiveData(){
        return this.backLiveData;
    }

    private void reloadPointStr() {
        Log.d(TAG, "reloadPointStr() " + orderType);
        if(orderType!=null){
            switch (orderType){
                case GO:
                    setOriginLiveData(item1Point==null?null:item1Point.getName());
                    setDestinationLiveData(clubPoint==null?null:clubPoint.getName());
                    setBackLiveData(null);
                    break;
                case BACK:
                    setOriginLiveData(clubPoint==null?null:clubPoint.getName());
                    setDestinationLiveData(item1Point==null?null:item1Point.getName());
                    setBackLiveData(null);
                    break;
                case GO_BACK:
                    setOriginLiveData(item1Point==null?null:item1Point.getName());
                    setDestinationLiveData(clubPoint==null?null:clubPoint.getName());
                    setBackLiveData(item2Point==null?null:item2Point.getName());
                    break;
            }
        }
        Log.d(TAG, "reloadPointStr().origin " + origin);
        Log.d(TAG, "reloadPointStr().destination " + destination);
        Log.d(TAG, "reloadPointStr().back " + back);
    }

    public void swap() {
        switch (orderType){
            case GO:
                setOrderTypeLiveData(OrderType.BACK); // This line change order type only
                setItem1LiveData(item1, item1Point); // To change item type
                setItem2LiveData(item2, item2Point); // To change item type
                break;
            case BACK:
                setOrderTypeLiveData(OrderType.GO); // This line change order type only
                setItem1LiveData(item1, item1Point); // To change item type
                setItem2LiveData(item2, item2Point); // To change item type
                break;
        }
    }

    public Point getOriginPoint() {
        if(orderType!=null) {
            switch (orderType) {
                case BACK:
                    return clubPoint;
                default:
                case GO:
                    return item1Point;
            }
        }
        return item1Point;
    }

    public Point getDestinationPoint() {
        if(orderType!=null) {
            switch (orderType) {
                case BACK:
                    return item1Point;
                default:
                case GO:
                    return clubPoint;
            }
        }
        return clubPoint;
    }

    public Point getBackPoint() {
        if(orderType!=null) {
            switch (orderType) {
                case BACK:
                    return null;
                default:
                case GO:
                    return item2Point;
            }
        }
        return item2Point;
    }

    private void mayBeInitItems() {
        if(items==null) {
            items = new ArrayList<>();
        }

        while (items.size()<3){
            items.add(null);
        }
    }

    public LiveData<RemoteLoaderResult<OrderWithDatas>> getPaymentResult() {
        return paymentResult;
    }

    public void setPaymentResult(RemoteLoaderResult<OrderWithDatas> paymentResult) {
        this.paymentResult.setValue(paymentResult);
    }

    public LiveData<RemoteLoaderResult<OrderWithDatas>> getCartResult() {
        return cartResult;
    }

    public void setCartResult(RemoteLoaderResult<OrderWithDatas> cartResult) {
        this.cartResult.setValue(cartResult);
    }

    public LiveData<RemoteLoaderResult<OrderWithDatas>> getCancelResult() {
        return cancelResult;
    }

    public void setCancelResult(RemoteLoaderResult<OrderWithDatas> cancelResult) {
        this.cancelResult.setValue(cancelResult);
    }

    public void attach(OrderWithDatas item) {
        this.setOrderLiveData(item.getOrder());
        this.setClubLiveData(item.getClub());
        this.setClubPointLiveData(item.getClubPoint());
        this.setCarLiveData(item.getCar());

        List<ItemWithDatas> items = item.getItems();
        if(items!=null) {
            for (int i=0; (i < items.size()) && (i < 2); i++) {
                ItemWithDatas itemWithData = items.get(i);
                if(itemWithData!=null){
                    if(i==0){
                        this.setItem1LiveData(itemWithData.getItem(), itemWithData.getPoint());
                    }else{
                        this.setItem2LiveData(itemWithData.getItem(), itemWithData.getPoint());
                    }
                }
            }
        }

        if(item.getOrder()!=null){
            if(item.getOrder().getType()!=null){
                switch (item.getOrder().getType()){
                    case Order.TYPE_GO:
                        setOrderTypeLiveData(OrderType.GO);
                        break;
                    case Order.TYPE_BACK:
                        setOrderTypeLiveData(OrderType.BACK);
                        break;
                    case Order.TYPE_GO_BACK:
                        setOrderTypeLiveData(OrderType.GO_BACK);
                        break;
                }
            }
        }
    }

    public LiveData<RemoteLoaderResult<OrderWithDatas>> getViewResult() {
        return viewResult;
    }

    public void setViewResult(RemoteLoaderResult<OrderWithDatas> viewResult) {
        this.viewResult.setValue(viewResult);
    }

    public MutableLiveData<Integer> getPlaceLiveData() {
        return placeLiveData;
    }

    public void setPlaceLiveData(Integer place) {
        this.place = place;
        this.placeLiveData.setValue(place);
    }

    public Integer getPlace() {
        return place;
    }
}

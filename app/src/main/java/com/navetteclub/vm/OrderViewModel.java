package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.OrderParam;
import com.navetteclub.api.models.OrderRequest;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.CashApiService;
import com.navetteclub.api.services.ClubApiService;
import com.navetteclub.api.services.OrderApiService;
import com.navetteclub.database.dao.PointDao_Impl;
import com.navetteclub.database.entity.Car;
import com.navetteclub.database.entity.CarAndModel;
import com.navetteclub.database.entity.Club;
import com.navetteclub.database.entity.ClubAndPoint;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.entity.ItemWithDatas;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.User;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderViewModel extends ViewModel {

    private static final String TAG = OrderViewModel.class.getSimpleName();

    private Club club;

    private MutableLiveData<Club> clubLiveData = new MutableLiveData<>();

    private Point clubPoint;

    private MutableLiveData<Point> clubPointLiveData = new MutableLiveData<>();

    private Order order;

    private MutableLiveData<Order> orderLiveData = new MutableLiveData<>();

    private Car car;

    private MutableLiveData<Car> carLiveData = new MutableLiveData<>();

    private ItemWithDatas goItem;

    private MutableLiveData<ItemWithDatas> goItemLiveData = new MutableLiveData<>();

    private ItemWithDatas backItem;

    private MutableLiveData<ItemWithDatas> backItemLiveData = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<List<CarAndModel>>> carsResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<OrderWithDatas>> orderResult = new MutableLiveData<>();

    public void loadCars(){
        loadCars(club);
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

    public void placeOrder(String token) {
        Log.d(TAG, "OrderApiService.placeOrder()");
        OrderRequest orderRequest = new OrderRequest(order, goItem, backItem);

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

    public LiveData<ItemWithDatas> getBackItemLiveData() {
        return backItemLiveData;
    }

    public void setBackItemLiveData(ItemWithDatas itemWithDatas) {
        this.backItem = itemWithDatas;
        this.backItemLiveData.setValue(itemWithDatas);
    }

    public LiveData<ItemWithDatas> getGoItemLiveData() {
        return goItemLiveData;
    }

    public void setGoItemLiveData(ItemWithDatas goItem) {
        this.goItem = goItem;
        this.goItemLiveData.setValue(goItem);
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

    public ItemWithDatas getGoItem() {
        return goItem;
    }

    public ItemWithDatas getBackItem() {
        return backItem;
    }

    public LiveData<Club> getClubLiveData() {
        return clubLiveData;
    }

    public void setClubLiveData(Club club) {
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
        this.car = car;
        this.carLiveData.setValue(car);
    }

    public Car getCar() {
        return car;
    }
}

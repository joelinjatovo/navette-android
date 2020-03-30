package com.joelinjatovo.navette.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.api.clients.RetrofitClient;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.api.services.NotificationApiService;
import com.joelinjatovo.navette.api.services.OrderApiService;
import com.joelinjatovo.navette.database.entity.Notification;
import com.joelinjatovo.navette.database.entity.OrderWithDatas;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.database.repositories.NotificationRepository;
import com.joelinjatovo.navette.models.RemoteLoaderResult;
import com.joelinjatovo.navette.utils.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersViewModel extends ViewModel {

    private static final String TAG = OrdersViewModel.class.getSimpleName();

    private MutableLiveData<RemoteLoaderResult<List<OrderWithDatas>>> ordersLiveData = new MutableLiveData<>();

    public void load(User user){
        Log.d(TAG, "service.load()");
        OrderApiService service = RetrofitClient.getInstance().create(OrderApiService.class);
        Call<RetrofitResponse<List<OrderWithDatas>>> call = service.getAll(user.getAuthorizationToken());
        call.enqueue(new Callback<RetrofitResponse<List<OrderWithDatas>>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<List<OrderWithDatas>>> call,
                                   @NonNull Response<RetrofitResponse<List<OrderWithDatas>>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG, response.body().toString());
                    ordersLiveData.setValue(new RemoteLoaderResult<>(response.body().getData()));
                }else{
                    ordersLiveData.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<List<OrderWithDatas>>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                ordersLiveData.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public MutableLiveData<RemoteLoaderResult<List<OrderWithDatas>>> getOrdersLiveData() {
        return ordersLiveData;
    }

    public void setOrdersLiveData(RemoteLoaderResult<List<OrderWithDatas>> result) {
        ordersLiveData.setValue(result);
    }
}
package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.NotificationApiService;
import com.navetteclub.api.services.OrderApiService;
import com.navetteclub.database.entity.Notification;
import com.navetteclub.database.entity.OrderWithDatas;
import com.navetteclub.database.entity.User;
import com.navetteclub.database.repositories.NotificationRepository;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersViewModel extends ViewModel {

    private static final String TAG = OrdersViewModel.class.getSimpleName();

    private MutableLiveData<RemoteLoaderResult<List<OrderWithDatas>>> ordersLiveData = new MutableLiveData<>();

    public void load(User user){
        Log.d(TAG, "OrderApiService.getAll( " +  user.getAuthorizationToken() + ")");
        OrderApiService service = RetrofitClient.getInstance().create(OrderApiService.class);
        Call<RetrofitResponse<List<OrderWithDatas>>> call = service.getAll(user.getAuthorizationToken());
        call.enqueue(new Callback<RetrofitResponse<List<OrderWithDatas>>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<List<OrderWithDatas>>> call,
                                   @NonNull Response<RetrofitResponse<List<OrderWithDatas>>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        ordersLiveData.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        switch (response.body().getCode()){
                            case 103:
                                ordersLiveData.setValue(new RemoteLoaderResult<>(R.string.error_invalid_access_token));
                            break;
                            default:
                                ordersLiveData.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
                            break;
                        }
                    }
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

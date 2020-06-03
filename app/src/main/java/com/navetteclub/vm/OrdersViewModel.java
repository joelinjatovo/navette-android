package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.Pagination;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.OrderApiService;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.User;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersViewModel extends ViewModel {

    private static final String TAG = OrdersViewModel.class.getSimpleName();

    private MutableLiveData<RemoteLoaderResult<List<Order>>> ordersResult = new MutableLiveData<>();

    private MutableLiveData<Pagination> paginationResult = new MutableLiveData<>();

    public void load(User user, int page){
        Log.d(TAG, "OrderApiService.getAll( " +  user.getAuthorizationToken() + ")");
        OrderApiService service = RetrofitClient.getInstance().create(OrderApiService.class);
        Call<RetrofitResponse<List<Order>>> call = service.index(user.getAuthorizationToken(), page);
        call.enqueue(new Callback<RetrofitResponse<List<Order>>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<List<Order>>> call,
                                   @NonNull Response<RetrofitResponse<List<Order>>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        paginationResult.setValue(response.body().getPagination());
                        ordersResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        ordersResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    ordersResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<List<Order>>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                ordersResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public LiveData<RemoteLoaderResult<List<Order>>> getOrdersResult() {
        return ordersResult;
    }

    public void setOrdersResult(RemoteLoaderResult<List<Order>> result) {
        ordersResult.setValue(result);
    }

    public LiveData<Pagination> getPaginationResult() {
        return paginationResult;
    }

    public void setPaginationResult(Pagination paginationResult) {
        this.paginationResult.setValue(paginationResult);
    }
}

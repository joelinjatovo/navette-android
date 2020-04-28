package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.ItemParam;
import com.navetteclub.api.models.RidePointParam;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.ItemApiService;
import com.navetteclub.api.services.RidePointApiService;
import com.navetteclub.database.entity.ItemWithDatas;
import com.navetteclub.database.entity.RideWithDatas;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemViewModel extends ViewModel {

    private static final String TAG = ItemViewModel.class.getSimpleName();

    private MutableLiveData<RemoteLoaderResult<ItemWithDatas>> itemViewResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<ItemWithDatas>> itemCancelResult = new MutableLiveData<>();

    public void loadItem(String token, String itemId) {
        ItemApiService service = RetrofitClient.getInstance().create(ItemApiService.class);
        Call<RetrofitResponse<ItemWithDatas>> call = service.getItem(token, itemId);
        call.enqueue(new Callback<RetrofitResponse<ItemWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<ItemWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<ItemWithDatas>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        itemViewResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        itemViewResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    itemViewResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<ItemWithDatas>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                itemViewResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void cancelItem(String token, String itemId){
        ItemApiService service = RetrofitClient.getInstance().create(ItemApiService.class);
        Call<RetrofitResponse<ItemWithDatas>> call = service.cancel(token, new ItemParam(itemId));
        call.enqueue(new Callback<RetrofitResponse<ItemWithDatas>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<ItemWithDatas>> call,
                                   @NonNull Response<RetrofitResponse<ItemWithDatas>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        itemCancelResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        itemCancelResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    itemCancelResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<ItemWithDatas>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                itemCancelResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public LiveData<RemoteLoaderResult<ItemWithDatas>> getItemViewResult() {
        return itemViewResult;
    }

    public void setItemViewResult(RemoteLoaderResult<ItemWithDatas> itemViewResult) {
        this.itemViewResult.setValue(itemViewResult);
    }

    public LiveData<RemoteLoaderResult<ItemWithDatas>> getItemCancelResult() {
        return itemCancelResult;
    }

    public void setItemCancelResult(RemoteLoaderResult<ItemWithDatas> itemCancelResult) {
        this.itemCancelResult.setValue(itemCancelResult);
    }
}

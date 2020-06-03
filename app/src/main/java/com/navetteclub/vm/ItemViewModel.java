package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.ItemParam;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.ItemApiService;
import com.navetteclub.database.entity.Item;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemViewModel extends ViewModel {

    private static final String TAG = ItemViewModel.class.getSimpleName();

    private MutableLiveData<RemoteLoaderResult<Item>> itemViewResult = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<Item>> itemCancelResult = new MutableLiveData<>();

    public void loadItem(String token, String itemId) {
        ItemApiService service = RetrofitClient.getInstance().create(ItemApiService.class);
        Call<RetrofitResponse<Item>> call = service.show(token, itemId);
        call.enqueue(new Callback<RetrofitResponse<Item>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<Item>> call,
                                   @NonNull Response<RetrofitResponse<Item>> response) {
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
            public void onFailure(@NonNull Call<RetrofitResponse<Item>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                itemViewResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public void cancelItem(String token, String itemId){
        ItemApiService service = RetrofitClient.getInstance().create(ItemApiService.class);
        Call<RetrofitResponse<Item>> call = service.cancel(token, new ItemParam(itemId));
        call.enqueue(new Callback<RetrofitResponse<Item>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<Item>> call,
                                   @NonNull Response<RetrofitResponse<Item>> response) {
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
            public void onFailure(@NonNull Call<RetrofitResponse<Item>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                itemCancelResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public LiveData<RemoteLoaderResult<Item>> getItemViewResult() {
        return itemViewResult;
    }

    public void setItemViewResult(RemoteLoaderResult<Item> itemViewResult) {
        this.itemViewResult.setValue(itemViewResult);
    }

    public LiveData<RemoteLoaderResult<Item>> getItemCancelResult() {
        return itemCancelResult;
    }

    public void setItemCancelResult(RemoteLoaderResult<Item> itemCancelResult) {
        this.itemCancelResult.setValue(itemCancelResult);
    }
}

package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.Pagination;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.NotificationApiService;
import com.navetteclub.database.entity.Notification;
import com.navetteclub.database.entity.User;
import com.navetteclub.database.repositories.NotificationRepository;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationViewModel extends ViewModel {

    private static final String TAG = NotificationViewModel.class.getSimpleName();

    private final NotificationRepository notificationRepository;

    private MutableLiveData<RemoteLoaderResult<List<Notification>>> notificationsResult = new MutableLiveData<>();

    private MutableLiveData<Pagination> paginationResult = new MutableLiveData<>();

    NotificationViewModel(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public LiveData<List<Notification>> getList() {
        return notificationRepository.getList();
    }

    public void load(User user, int page){
        Log.d(TAG, "service.load()");
        NotificationApiService service = RetrofitClient.getInstance().create(NotificationApiService.class);
        Call<RetrofitResponse<List<Notification>>> call = service.getAll(user.getAuthorizationToken(), page);
        call.enqueue(new Callback<RetrofitResponse<List<Notification>>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<List<Notification>>> call,
                                   @NonNull Response<RetrofitResponse<List<Notification>>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null) {
                    Log.d(TAG, response.body().toString());
                    if(response.body().isSuccess()) {
                        paginationResult.setValue(response.body().getPagination());
                        notificationsResult.setValue(new RemoteLoaderResult<>(response.body().getData()));
                    }else{
                        notificationsResult.setValue(new RemoteLoaderResult<>(response.body().getErrorResString()));
                    }
                }else{
                    notificationsResult.setValue(new RemoteLoaderResult<>(R.string.error_unkown));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<List<Notification>>> call, @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                notificationsResult.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public LiveData<RemoteLoaderResult<List<Notification>>> getNotificationResult() {
        return notificationsResult;
    }

    public void setNotificationsResult(RemoteLoaderResult<List<Notification>> result) {
        notificationsResult.setValue(result);
    }

    public MutableLiveData<Pagination> getPaginationResult() {
        return paginationResult;
    }

    public void setPaginationResult(Pagination paginationResult) {
        this.paginationResult.setValue(paginationResult);
    }
}

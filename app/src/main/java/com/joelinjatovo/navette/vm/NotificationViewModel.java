package com.joelinjatovo.navette.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.api.clients.RetrofitClient;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.api.services.NotificationApiService;
import com.joelinjatovo.navette.database.entity.Notification;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.database.repositories.NotificationRepository;
import com.joelinjatovo.navette.models.RemoteLoaderResult;
import com.joelinjatovo.navette.utils.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationViewModel extends ViewModel {

    private static final String TAG = ClubViewModel.class.getSimpleName();

    private final NotificationRepository notificationRepository;

    private MutableLiveData<RemoteLoaderResult<List<Notification>>> notificationsLiveData = new MutableLiveData<>();

    NotificationViewModel(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public LiveData<List<Notification>> getList() {
        return notificationRepository.getList();
    }

    public void load(User user){
        Log.d(TAG, "service.getClubs()");
        NotificationApiService service = RetrofitClient.getInstance().create(NotificationApiService.class);
        Call<RetrofitResponse<List<Notification>>> call = service.getAll(user.getAuthorizationToken());
        call.enqueue(new Callback<RetrofitResponse<List<Notification>>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<List<Notification>>> call,
                                   @NonNull Response<RetrofitResponse<List<Notification>>> response) {
                Log.d(TAG, response.toString());
                if (response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG, response.body().toString());
                    notificationsLiveData.setValue(new RemoteLoaderResult<>(response.body().getData()));
                }else{
                    notificationsLiveData.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<List<Notification>>> call, @NonNull Throwable throwable) {
                Log.e(TAG, throwable.toString(), throwable);
                notificationsLiveData.setValue(new RemoteLoaderResult<>(R.string.error_bad_request));
            }
        });
    }

    public MutableLiveData<RemoteLoaderResult<List<Notification>>> getNotificationsLiveData() {
        return notificationsLiveData;
    }
}

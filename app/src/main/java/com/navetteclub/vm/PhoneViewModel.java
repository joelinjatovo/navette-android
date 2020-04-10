package com.navetteclub.vm;

import android.util.MalformedJsonException;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.Register;
import com.navetteclub.api.repositories.LoginRepository;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.UserApiService;
import com.navetteclub.database.entity.User;
import com.navetteclub.models.LoginFormState;
import com.navetteclub.models.PhoneFormState;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneViewModel extends ViewModel {

    private static final String TAG = PhoneViewModel.class.getSimpleName();

    private MutableLiveData<PhoneFormState> phoneFormState = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<User>> phoneResult = new MutableLiveData<>();

    public LiveData<PhoneFormState> getPhoneFormState() {
        return phoneFormState;
    }

    public void setPhoneResult(RemoteLoaderResult<User> result) {
        phoneResult.setValue(result);
    }

    public LiveData<RemoteLoaderResult<User>> getPhoneResult() {
        return phoneResult;
    }

    public void phoneDataChanged(String prefix, String phone) {
        if (!isPhoneValid(prefix, phone)) {
            phoneFormState.setValue(new PhoneFormState(R.string.invalid_password));
        } else {
            phoneFormState.setValue(new PhoneFormState(true));
        }
    }

    public void update(User user){
        Register register = new Register(user);
        UserApiService service = RetrofitClient.getInstance().create(UserApiService.class);
        Call<RetrofitResponse<User>> call = service.updateUser(user.getAuthorizationToken(), register);
        call.enqueue(new Callback<RetrofitResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<User>> call,
                                   @NonNull Response<RetrofitResponse<User>> response) {
                Log.d(TAG, response.toString());
                if(response.body()!=null){
                    Log.d(TAG, "response " + response.body().getData());
                    if(response.body().isSuccess()){
                        Log.d(TAG, response.body().getData().toString());
                        phoneResult.setValue(new RemoteLoaderResult<User>(response.body().getData()));
                    }else{
                        phoneResult.setValue(new RemoteLoaderResult<User>(response.body().getStatusResString()));
                    }
                }else{
                    phoneResult.setValue(new RemoteLoaderResult<User>(R.string.update_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<User>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
                phoneResult.setValue(new RemoteLoaderResult<User>(R.string.update_failed));
            }
        });
    }

    // A placeholder phone validation check
    private boolean isPhoneValid(String prefix, String phone) {
        if (prefix == null) {
            return false;
        }
        if (phone == null) {
            return false;
        }
        return !(prefix+phone).trim().isEmpty();
    }
}

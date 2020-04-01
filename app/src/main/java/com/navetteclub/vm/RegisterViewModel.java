package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.repositories.RegisterRepository;
import com.navetteclub.database.entity.UserWithRoles;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel implements Callback<RetrofitResponse<UserWithRoles>> {

    private static final String TAG = RegisterViewModel.class.getSimpleName();

    private MutableLiveData<RemoteLoaderResult<UserWithRoles>> registerResult = new MutableLiveData<>();

    private RegisterRepository registerRepository;

    public RegisterViewModel(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    public LiveData<RemoteLoaderResult<UserWithRoles>> getRegisterResult() {
        return registerResult;
    }

    public void register(String name, String phone, String password) {
        registerRepository.register(name, phone, password, this);
    }

    @Override
    public void onResponse(@NonNull Call<RetrofitResponse<UserWithRoles>> call, @NonNull Response<RetrofitResponse<UserWithRoles>> response) {
        Log.d(TAG, response.toString());
        if(response.body()!=null){
            if( null != response.body().getData()){
                Log.d(TAG, response.body().getData().toString());
                registerResult.setValue(new RemoteLoaderResult(response.body().getData()));
            }else{
                registerResult.setValue(new RemoteLoaderResult(R.string.login_failed));
            }
        }else{
            registerResult.setValue(new RemoteLoaderResult(R.string.login_failed));
        }
    }

    @Override
    public void onFailure(@NonNull Call<RetrofitResponse<UserWithRoles>> call, @NonNull Throwable t) {
        Log.e(TAG, t.getMessage());
        registerResult.setValue(new RemoteLoaderResult(R.string.register_failed));
    }
}

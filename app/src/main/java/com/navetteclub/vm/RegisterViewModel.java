package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.models.Register;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.repositories.RegisterRepository;
import com.navetteclub.database.entity.User;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel implements Callback<RetrofitResponse<User>> {

    private static final String TAG = RegisterViewModel.class.getSimpleName();

    private MutableLiveData<RemoteLoaderResult<User>> registerResult = new MutableLiveData<>();

    private RegisterRepository registerRepository;

    public RegisterViewModel(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    public LiveData<RemoteLoaderResult<User>> getRegisterResult() {
        return registerResult;
    }

    public void setRegisterResult(RemoteLoaderResult<User> registerResult) {
        Log.d(TAG, "'setRegisterResult(registerResult)'");
        this.registerResult.setValue(registerResult);
    }

    public void register(String name, String phone, String password) {
        registerRepository.register(name, phone, password, this);
    }

    public void registerViaFacebook(Register register) {
        registerRepository.registerViaFacebook(register, this);
    }

    @Override
    public void onResponse(@NonNull Call<RetrofitResponse<User>> call, @NonNull Response<RetrofitResponse<User>> response) {
        Log.d(TAG, response.toString());
        if(response.body()!=null){
            Log.d(TAG, "response " + response.body().getData());
            if(response.body().isSuccess()){
                Log.d(TAG, response.body().getData().toString());
                registerResult.setValue(new RemoteLoaderResult<User>(response.body().getData()));
            }else{
                registerResult.setValue(new RemoteLoaderResult<User>(response.body().getErrorResString()));
            }
        }else{
            registerResult.setValue(new RemoteLoaderResult<User>(R.string.register_failed));
        }
    }

    @Override
    public void onFailure(@NonNull Call<RetrofitResponse<User>> call, @NonNull Throwable t) {
        Log.e(TAG, t.getMessage());
        registerResult.setValue(new RemoteLoaderResult<User>(R.string.register_failed));
    }
}

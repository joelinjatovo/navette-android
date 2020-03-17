package com.joelinjatovo.navette.ui.main.auth.register;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.data.repositories.RegisterRepository;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.ui.main.auth.register.RegisterResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel implements Callback<RetrofitResponse<User>> {

    private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();

    private RegisterRepository registerRepository;

    public RegisterViewModel(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    public LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String name, String phone, String password) {
        registerRepository.register(name, phone, password, this);
    }

    @Override
    public void onResponse(@NonNull Call<RetrofitResponse<User>> call, @NonNull Response<RetrofitResponse<User>> response) {
        RetrofitResponse<User> data = response.body();
        if(null != data){
            User user = data.getData();
            if( 0 != data.getCode() && null != user){
                registerResult.setValue(new RegisterResult(user));
            }else{
                registerResult.setValue(new RegisterResult(R.string.login_failed));
            }
        }else{
            registerResult.setValue(new RegisterResult(R.string.login_failed));
        }
    }

    @Override
    public void onFailure(@NonNull Call<RetrofitResponse<User>> call, @NonNull Throwable t) {
        registerResult.setValue(new RegisterResult(R.string.register_failed));
    }
}

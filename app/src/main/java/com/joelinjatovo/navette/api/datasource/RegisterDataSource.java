package com.joelinjatovo.navette.api.datasource;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.api.clients.RetrofitClient;
import com.joelinjatovo.navette.api.data.Login;
import com.joelinjatovo.navette.api.data.Register;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.api.services.TokenApiService;
import com.joelinjatovo.navette.api.services.UserApiService;
import com.joelinjatovo.navette.data.source.RegisterDataSourceBase;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.ui.auth.login.LoginResult;
import com.joelinjatovo.navette.ui.auth.register.RegisterResult;
import com.joelinjatovo.navette.utils.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class RegisterDataSource implements RegisterDataSourceBase {

    public void register(String name, String phone, String password,  MutableLiveData<RegisterResult> registerResultMutableLiveData) {
        Register registrationData = new Register(name, phone, password);

        Log.d("RegisterDataSource", "service.register" + name + " "+ phone + " " + password);

        UserApiService service = RetrofitClient.getInstance().create(UserApiService.class);
        Call<RetrofitResponse<User>> call = service.register(registrationData);
        call.enqueue(new Callback<RetrofitResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<User>> call, @NonNull Response<RetrofitResponse<User>> response) {
                Log.d("RegisterDataSource", response.toString());
                RetrofitResponse<User> data = response.body();
                if(null != data){
                    Log.d("RegisterDataSource", data.toString());
                    User user = data.getData();
                    if( 0 != data.getCode() && null != user){
                        registerResultMutableLiveData.setValue(new RegisterResult(user));
                    }else{
                        Log.d("RegisterDataSource", response.code() + "  " + response.message());
                        registerResultMutableLiveData.setValue(new RegisterResult(R.string.login_failed));
                    }
                }else{
                    registerResultMutableLiveData.setValue(new RegisterResult(R.string.login_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<User>> call, @NonNull Throwable throwable) {
                Log.w("LoginDataSource", throwable);
                registerResultMutableLiveData.setValue(new RegisterResult(R.string.register_failed));
            }
        });
    }
}

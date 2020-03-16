package com.joelinjatovo.navette.api.datasource;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.api.clients.RetrofitClient;
import com.joelinjatovo.navette.api.data.Login;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.api.services.TokenApiService;
import com.joelinjatovo.navette.data.source.LoginDataSourceBase;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.ui.auth.login.LoginResult;
import com.joelinjatovo.navette.utils.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource implements LoginDataSourceBase {

    public void login(String phone, String password,  MutableLiveData<LoginResult> loginResultMutableLiveData) {
        Login login = new Login(phone, password);

        Log.d("LoginDataSource", "service.getToken" + phone + " " + password);

        TokenApiService service = RetrofitClient.getInstance().create(TokenApiService.class);
        Call<RetrofitResponse<User>> call = service.getToken(login);
        call.enqueue(new Callback<RetrofitResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<User>> call, @NonNull Response<RetrofitResponse<User>> response) {
                Log.d("LoginDataSource", response.toString());

                RetrofitResponse<User> data = response.body();
                if(null != data){
                    Log.d("LoginDataSource", data.toString());
                    User user = data.getData();
                    switch(data.getCode()){
                        case 0:
                            if(null != user){
                                loginResultMutableLiveData.setValue(new LoginResult(user));
                                return;
                            }
                        break;
                        case 100:
                            loginResultMutableLiveData.setValue(new LoginResult(R.string.invalid_phone_and_password));
                        return;
                        case 101:
                            loginResultMutableLiveData.setValue(new LoginResult(R.string.error_unauthorized));
                        return;
                        case 102:
                            loginResultMutableLiveData.setValue(new LoginResult(R.string.error_bad_request));
                        return;
                        case 103:
                            loginResultMutableLiveData.setValue(new LoginResult(R.string.error_not_found));
                        return;
                        case 104:
                            loginResultMutableLiveData.setValue(new LoginResult(R.string.error_500));
                        return;
                    }
                }

                loginResultMutableLiveData.setValue(new LoginResult(R.string.login_failed));
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<User>> call, @NonNull Throwable throwable) {
                Log.w("LoginDataSource", throwable);
                loginResultMutableLiveData.setValue(new LoginResult(R.string.login_failed));
            }
        });
    }

    public void logout() {
        // TODO: revoke authentication
    }
}

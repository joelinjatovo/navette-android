package com.joelinjatovo.navette.api.datasource;

import androidx.lifecycle.MutableLiveData;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.api.clients.RetrofitClient;
import com.joelinjatovo.navette.api.data.Login;
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

        Log.d("LoginDataSource", phone + " " + password);

        TokenApiService service = RetrofitClient.getInstance().create(TokenApiService.class);
        Call<User> call = service.getToken(login);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("LoginDataSource", response.message());
                User data = response.body();
                loginResultMutableLiveData.setValue(new LoginResult(data));
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                Log.w("LoginDataSource", throwable);
                loginResultMutableLiveData.setValue(new LoginResult(R.string.login_failed));
            }
        });
    }

    public void logout() {
        // TODO: revoke authentication
    }
}

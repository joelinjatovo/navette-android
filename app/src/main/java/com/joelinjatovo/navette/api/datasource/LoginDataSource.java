package com.joelinjatovo.navette.api.datasource;

import com.joelinjatovo.navette.api.clients.RetrofitClient;
import com.joelinjatovo.navette.api.data.Login;
import com.joelinjatovo.navette.api.services.TokenApiService;
import com.joelinjatovo.navette.data.source.LoginDataSourceBase;
import com.joelinjatovo.navette.data.Result;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.utils.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource implements LoginDataSourceBase {

    public Result<User> login(String phone, String password) {
        Login login = new Login(phone, password);

        Log.d("LoginDataSource", phone + " " + password);

        TokenApiService service = RetrofitClient.getInstance().create(TokenApiService.class);
        Call<User> call = service.getToken(login);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("LoginDataSource", response.message());
                //return new Result.Success<User>(response.body());
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                //return new Result.Error(new IOException("Error logging in", throwable));
            }
        });

        return new Result.Error(new IOException("Error logging in"));
    }

    public void logout() {
        // TODO: revoke authentication
    }
}

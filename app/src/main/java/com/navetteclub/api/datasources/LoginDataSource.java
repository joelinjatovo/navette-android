package com.navetteclub.api.datasources;

import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.Login;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.TokenApiService;
import com.navetteclub.database.entity.User;
import com.navetteclub.datasource.LoginDataSourceBase;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource implements LoginDataSourceBase {

    public void login(String phone, String password, Callback<RetrofitResponse<User>> callback) {
        Login login = new Login(phone, password);
        TokenApiService service = RetrofitClient.getInstance().create(TokenApiService.class);
        Call<RetrofitResponse<User>> call = service.getToken(login);
        call.enqueue(callback);
    }

    public void logout() {
        // TODO: revoke authentication
    }
}

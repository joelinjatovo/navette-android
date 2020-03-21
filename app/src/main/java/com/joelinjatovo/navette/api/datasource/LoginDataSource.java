package com.joelinjatovo.navette.api.datasource;

import com.joelinjatovo.navette.api.clients.RetrofitClient;
import com.joelinjatovo.navette.api.models.Login;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.api.services.TokenApiService;
import com.joelinjatovo.navette.data.source.LoginDataSourceBase;
import com.joelinjatovo.navette.database.entity.UserWithRoles;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource implements LoginDataSourceBase {

    public void login(String phone, String password, Callback<RetrofitResponse<UserWithRoles>> callback) {
        Login login = new Login(phone, password);
        TokenApiService service = RetrofitClient.getInstance().create(TokenApiService.class);
        Call<RetrofitResponse<UserWithRoles>> call = service.getToken(login);
        call.enqueue(callback);
        /*
        call.enqueue(new Callback<RetrofitResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<User>> call, @NonNull Response<RetrofitResponse<User>> response) {

            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<User>> call, @NonNull Throwable throwable) {
                Log.w("LoginDataSource", throwable);
            }
        });
         */
    }

    public void logout() {
        // TODO: revoke authentication
    }
}

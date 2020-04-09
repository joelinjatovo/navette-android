package com.navetteclub.api.datasources;

import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.Register;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.UserApiService;
import com.navetteclub.database.entity.User;
import com.navetteclub.datasource.RegisterDataSourceBase;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class RegisterDataSource implements RegisterDataSourceBase {

    public void register(String name, String phone, String password, Callback<RetrofitResponse<User>> callback) {
        Register registrationData = new Register(name, phone, password);
        UserApiService service = RetrofitClient.getInstance().create(UserApiService.class);
        Call<RetrofitResponse<User>> call = service.register(registrationData);
        call.enqueue(callback);
    }

    public void registerViaFacebook(Register registrationData, Callback<RetrofitResponse<User>> callback) {
        UserApiService service = RetrofitClient.getInstance().create(UserApiService.class);
        Call<RetrofitResponse<User>> call = service.registerViaFacebook(registrationData);
        call.enqueue(callback);
    }
}

package com.navetteclub.api.datasources;

import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.Register;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.UserApiService;
import com.navetteclub.datasource.RegisterDataSourceBase;
import com.navetteclub.database.entity.UserWithRoles;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class RegisterDataSource implements RegisterDataSourceBase {

    public void register(String name, String phone, String password, Callback<RetrofitResponse<UserWithRoles>> callback) {
        Register registrationData = new Register(name, phone, password);
        UserApiService service = RetrofitClient.getInstance().create(UserApiService.class);
        Call<RetrofitResponse<UserWithRoles>> call = service.register(registrationData);
        call.enqueue(callback);
    }
}

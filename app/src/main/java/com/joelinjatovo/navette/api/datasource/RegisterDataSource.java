package com.joelinjatovo.navette.api.datasource;

import com.joelinjatovo.navette.api.clients.RetrofitClient;
import com.joelinjatovo.navette.api.models.Register;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.api.services.UserApiService;
import com.joelinjatovo.navette.data.source.RegisterDataSourceBase;
import com.joelinjatovo.navette.database.entity.UserWithRoles;

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

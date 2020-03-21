package com.joelinjatovo.navette.vm;

import android.util.MalformedJsonException;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.joelinjatovo.navette.R;
import com.joelinjatovo.navette.api.responses.RetrofitResponse;
import com.joelinjatovo.navette.api.repositories.LoginRepository;
import com.joelinjatovo.navette.database.entity.UserWithRoles;
import com.joelinjatovo.navette.models.LoginFormState;
import com.joelinjatovo.navette.models.RemoteLoaderResult;
import com.joelinjatovo.navette.utils.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel implements Callback<RetrofitResponse<UserWithRoles>> {

    private static final String TAG = LoginViewModel.class.getSimpleName();

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<UserWithRoles>> loginResult = new MutableLiveData<>();

    private LoginRepository loginRepository;

    LoginViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    public LiveData<RemoteLoaderResult<UserWithRoles>> getLoginResult() {
        return loginResult;
    }

    public void login(String phone, String password) {
        loginRepository.login(phone, password, this);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    @Override
    public void onResponse(@NonNull Call<RetrofitResponse<UserWithRoles>> call, Response<RetrofitResponse<UserWithRoles>> response) {
        Log.d(TAG, response.toString());

        RetrofitResponse<UserWithRoles> data = response.body();
        if(null != data){
            Log.d(TAG, data.toString());
            UserWithRoles item = data.getData();
            switch(data.getCode()){
                case 0:
                    if(null != item){
                        loginResult.setValue(new RemoteLoaderResult(item));
                        return;
                    }
                    break;
                case 100:
                    loginResult.setValue(new RemoteLoaderResult(R.string.invalid_phone_and_password));
                    return;
                case 101:
                    loginResult.setValue(new RemoteLoaderResult(R.string.error_unauthorized));
                    return;
                case 102:
                    loginResult.setValue(new RemoteLoaderResult(R.string.error_bad_request));
                    return;
                case 103:
                    loginResult.setValue(new RemoteLoaderResult(R.string.error_not_found));
                    return;
                case 104:
                    loginResult.setValue(new RemoteLoaderResult(R.string.error_500));
                    return;
            }
        }

        loginResult.setValue(new RemoteLoaderResult(R.string.login_failed));
    }

    @Override
    public void onFailure(@NonNull Call<RetrofitResponse<UserWithRoles>> call, @NonNull Throwable t) {
        Log.e(TAG, t.getMessage(), t);
        if(t instanceof MalformedJsonException){
            loginResult.setValue(new RemoteLoaderResult(R.string.invalid_json_response));
        }else{
            loginResult.setValue(new RemoteLoaderResult(R.string.login_failed));
        }
    }
}

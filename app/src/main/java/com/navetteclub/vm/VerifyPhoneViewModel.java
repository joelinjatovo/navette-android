package com.navetteclub.vm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.Register;
import com.navetteclub.api.models.VerifyCode;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.UserApiService;
import com.navetteclub.database.entity.User;
import com.navetteclub.models.PhoneFormState;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.models.VerifyPhoneFormState;
import com.navetteclub.utils.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyPhoneViewModel extends ViewModel {

    private static final String TAG = VerifyPhoneViewModel.class.getSimpleName();

    private MutableLiveData<VerifyPhoneFormState> verifyPhoneFormState = new MutableLiveData<>();

    private MutableLiveData<RemoteLoaderResult<User>> verifyPhoneResult = new MutableLiveData<>();
    private MutableLiveData<RemoteLoaderResult<Object>> resendCodeResult = new MutableLiveData<>();

    public LiveData<VerifyPhoneFormState> getVerifyPhoneFormState() {
        return verifyPhoneFormState;
    }

    public void verifyCodeDataChanged(String code) {
        if (!isCodeValid(code)) {
            verifyPhoneFormState.setValue(new VerifyPhoneFormState(R.string.invalid_password));
        } else {
            verifyPhoneFormState.setValue(new VerifyPhoneFormState(true));
        }
    }

    public void verify(User user, String code){
        Register register = new Register(user);
        UserApiService service = RetrofitClient.getInstance().create(UserApiService.class);
        Call<RetrofitResponse<User>> call = service.verify(user.getAuthorizationToken(), new VerifyCode(code));
        call.enqueue(new Callback<RetrofitResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<User>> call,
                                   @NonNull Response<RetrofitResponse<User>> response) {
                Log.d(TAG, response.toString());
                if(response.body()!=null){
                    Log.d(TAG, "response " + response.body());
                    if(response.body().isSuccess()){
                        Log.d(TAG, "Data " +response.body().getData());
                        verifyPhoneResult.setValue(new RemoteLoaderResult<User>(response.body().getData()));
                    }else{
                        verifyPhoneResult.setValue(new RemoteLoaderResult<User>(response.body().getStatusResString()));
                    }
                }else{
                    verifyPhoneResult.setValue(new RemoteLoaderResult<User>(R.string.update_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<User>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
                verifyPhoneResult.setValue(new RemoteLoaderResult<User>(R.string.update_failed));
            }
        });
    }

    public void resend(User user) {
        UserApiService service = RetrofitClient.getInstance().create(UserApiService.class);
        Call<RetrofitResponse<Object>> call = service.resend(user.getAuthorizationToken());
        call.enqueue(new Callback<RetrofitResponse<Object>>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse<Object>> call,
                                   @NonNull Response<RetrofitResponse<Object>> response) {
                Log.d(TAG, response.toString());
                if(response.body()!=null){
                    Log.d(TAG, "response " + response.body());
                    if(response.body().isSuccess()){
                        Log.d(TAG, "Data " + response.body().getData());
                        resendCodeResult.setValue(new RemoteLoaderResult<Object>(response.body().getData()));
                    }else{
                        resendCodeResult.setValue(new RemoteLoaderResult<Object>(response.body().getStatusResString()));
                    }
                }else{
                    resendCodeResult.setValue(new RemoteLoaderResult<Object>(R.string.update_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse<Object>> call,
                                  @NonNull Throwable throwable) {
                Log.e(TAG, throwable.getMessage());
                resendCodeResult.setValue(new RemoteLoaderResult<Object>(R.string.update_failed));
            }
        });
    }

    // A placeholder phone validation check
    private boolean isCodeValid(String code) {
        if (code == null) {
            return false;
        }
        return code.trim().length() == 4;
    }

    public LiveData<RemoteLoaderResult<User>> getVerifyPhoneResult() {
        return verifyPhoneResult;
    }

    public void setVerifyPhoneResult(RemoteLoaderResult<User> verifyPhoneResult) {
        this.verifyPhoneResult.setValue(verifyPhoneResult);
    }

    public LiveData<RemoteLoaderResult<Object>> getResendCodeResult() {
        return resendCodeResult;
    }

    public void setResendCodeResult(RemoteLoaderResult<Object> resendCodeResult) {
        this.resendCodeResult.setValue(resendCodeResult);
    }
}

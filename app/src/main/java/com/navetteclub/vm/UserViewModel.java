package com.navetteclub.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.navetteclub.R;
import com.navetteclub.api.clients.RetrofitClient;
import com.navetteclub.api.models.Register;
import com.navetteclub.api.responses.RetrofitResponse;
import com.navetteclub.api.services.UserApiService;
import com.navetteclub.database.callback.UpsertCallback;
import com.navetteclub.database.entity.User;
import com.navetteclub.database.repositories.UserRepository;
import com.navetteclub.models.RemoteLoaderResult;
import com.navetteclub.utils.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel  extends ViewModel {

    private static final String TAG = UserViewModel.class.getSimpleName();

    private static UserViewModel instance;

    private UserRepository repository;

    private LiveData<List<User>> listLiveData;

    public static synchronized UserViewModel getInstance(Application application) {
        if (instance == null) {
            instance = new UserViewModel(application);
            return instance;
        }
        return instance;
    }

    UserViewModel(Application application){
        repository = new UserRepository(application);
        listLiveData = repository.getList();
    }

    public LiveData<List<User>> getList(){
        return listLiveData;
    }

    public void insert(UpsertCallback<User> callback, User... users){
        repository.insert(callback, users);
    }

    public void upsert(UpsertCallback<User> callback, User... users){
        repository.upsert(callback, users);
    }
}

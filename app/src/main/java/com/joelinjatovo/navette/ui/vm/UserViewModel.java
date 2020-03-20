package com.joelinjatovo.navette.ui.vm;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.joelinjatovo.navette.database.callback.UpsertCallback;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.database.entity.UserWithPoints;
import com.joelinjatovo.navette.database.entity.UserWithRoles;
import com.joelinjatovo.navette.database.repository.UserRepository;

import java.util.List;

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

    public void insertUserWithRoles(UpsertCallback<UserWithRoles> callback, UserWithRoles... users){
        repository.insertUserWithRoles(callback, users);
    }

}

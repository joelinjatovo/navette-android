package com.joelinjatovo.navette.ui.vm;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.database.repository.UserRepository;

import java.util.List;

public class UserViewModel {
    private UserRepository repository;
    private LiveData<List<User>> listLiveData;

    UserViewModel(Application application){
        repository = new UserRepository(application);
        listLiveData = repository.getList();
    }

    public LiveData<List<User>> getList(){
        return listLiveData;
    }

    void insert(User... users){
        repository.insert(users);
    }
}

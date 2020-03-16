package com.joelinjatovo.navette.ui.vm;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.database.repository.UserRepository;

import java.util.List;

public class UserViewModel  extends ViewModel {
    private UserRepository repository;
    private LiveData<List<User>> listLiveData;

    UserViewModel(Application application){
        repository = new UserRepository(application);
        listLiveData = repository.getList();
    }

    public LiveData<List<User>> getList(){
        return listLiveData;
    }

    public void insert(UserRepository.Callback callback, User... users){
        repository.insert(callback, users);
    }
}

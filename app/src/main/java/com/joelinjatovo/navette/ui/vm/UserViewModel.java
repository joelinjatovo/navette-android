package com.joelinjatovo.navette.ui.vm;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.database.repository.UserRepository;

import java.util.List;

public class UserViewModel  extends ViewModel {

    private static UserViewModel instance;

    private UserRepository repository;

    private MutableLiveData<User> currentUser;

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

    public void insert(UserRepository.Callback callback, User... users){
        repository.insert(callback, users);
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser.setValue(currentUser);
    }
}

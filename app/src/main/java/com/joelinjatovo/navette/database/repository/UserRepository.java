package com.joelinjatovo.navette.database.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.joelinjatovo.navette.database.AppDatabase;
import com.joelinjatovo.navette.database.callback.UpsertCallback;
import com.joelinjatovo.navette.database.dao.UserDao;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.database.task.UpsertAsyncTask;
import com.joelinjatovo.navette.utils.Log;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private UserDao userDao;

    private LiveData<List<User>> listLiveData;

    public UserRepository(Application application){
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
        listLiveData = userDao.load();
    }

    public LiveData<List<User>> getList(){
        return listLiveData;
    }

    public void insert(UpsertCallback<User> callback, User... users){
        (new UpsertAsyncTask(userDao, callback)).execute(users);
    }
}

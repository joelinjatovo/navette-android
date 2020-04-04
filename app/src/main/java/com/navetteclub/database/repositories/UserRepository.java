package com.navetteclub.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.navetteclub.database.AppDatabase;
import com.navetteclub.database.callback.FindCallback;
import com.navetteclub.database.callback.UpsertCallback;
import com.navetteclub.database.dao.UserDao;
import com.navetteclub.database.entity.User;
import com.navetteclub.database.task.UpsertAsyncTask;
import com.navetteclub.database.task.UpsertUserWithRolesAsyncTask;
import com.navetteclub.database.task.UserFindAsyncTask;

import java.util.List;

public class UserRepository {

    private static UserRepository instance;

    private UserDao userDao;

    private LiveData<List<User>> listLiveData;

    public UserRepository(Application application){
        AppDatabase database = AppDatabase.getInstance(application);
        userDao = database.userDao();
        listLiveData = userDao.load();
    }

    public static synchronized UserRepository getInstance(Application application) {
        if(instance == null){
            instance = new UserRepository(application);
        }
        return instance;
    }

    public LiveData<List<User>> getList(){
        return listLiveData;
    }

    public void insert(UpsertCallback<User> callback, User... users){
        (new UpsertAsyncTask<User>(userDao, callback)).execute(users);
    }

    public void upsert(UpsertCallback<User> callback, User... users){
        (new UpsertUserWithRolesAsyncTask(userDao, callback)).execute(users);
    }

    public void find(FindCallback<User> callback, Long userId) {
        (new UserFindAsyncTask(userDao, callback)).execute(userId);
    }
}

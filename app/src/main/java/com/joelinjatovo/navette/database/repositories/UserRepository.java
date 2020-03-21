package com.joelinjatovo.navette.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.joelinjatovo.navette.database.AppDatabase;
import com.joelinjatovo.navette.database.callback.FindCallback;
import com.joelinjatovo.navette.database.callback.UpsertCallback;
import com.joelinjatovo.navette.database.dao.UserDao;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.database.entity.UserWithRoles;
import com.joelinjatovo.navette.database.task.UpsertAsyncTask;
import com.joelinjatovo.navette.database.task.UpsertUserWithRolesAsyncTask;
import com.joelinjatovo.navette.database.task.UserFindAsyncTask;

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

    public void insertUserWithRoles(UpsertCallback<UserWithRoles> callback, UserWithRoles... users){
        (new UpsertUserWithRolesAsyncTask(userDao, callback)).execute(users);
    }

    public void find(FindCallback<User> callback, Long userId) {
        (new UserFindAsyncTask(userDao, callback)).execute(userId);
    }
}

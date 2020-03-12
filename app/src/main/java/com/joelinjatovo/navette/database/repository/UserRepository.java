package com.joelinjatovo.navette.database.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.joelinjatovo.navette.database.AppDatabase;
import com.joelinjatovo.navette.database.dao.UserDao;
import com.joelinjatovo.navette.database.entity.User;

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

    public void insert(User... users){
        new InsertAsyncTask(userDao).execute(users);
    }

    private static class InsertAsyncTask extends AsyncTask<User, Void, Void> {

        UserDao mAsyncTaskDao;

        InsertAsyncTask(UserDao dao){
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(User... users) {
            mAsyncTaskDao.insert(users);
            return null;
        }
    }
}
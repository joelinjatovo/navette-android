package com.joelinjatovo.navette.database.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.joelinjatovo.navette.database.AppDatabase;
import com.joelinjatovo.navette.database.dao.UserDao;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.utils.Log;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static final String TAG = UserRepository.class.getSimpleName();

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

    public void insert(Callback callback, User... users){
        new UpsertAsyncTask(userDao, callback).execute(users);
    }

    private static class UpsertAsyncTask extends AsyncTask<User, Void, List<User>> {

        private static final String TAG = UpsertAsyncTask.class.getSimpleName();

        UserDao mAsyncTaskDao;

        Callback callback;

        UpsertAsyncTask(UserDao dao, Callback callback){
            mAsyncTaskDao = dao;
            this.callback = callback;
        }

        @Override
        protected  List<User> doInBackground(User... users) {
            try {
                return mAsyncTaskDao.upsert(users);
            }catch (Exception e){
                Log.e(TAG, e.getMessage());
                cancel(true);
                return null;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if(callback!=null){
                callback.onError();
            }
        }

        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
            if(callback!=null){
                if( users != null && !users.isEmpty()){
                    callback.onSuccess(users);
                }else{
                    callback.onError();
                }
            }
        }
    }

    public interface Callback{
        void onError();
        void onSuccess(List<User>users);
    }
}

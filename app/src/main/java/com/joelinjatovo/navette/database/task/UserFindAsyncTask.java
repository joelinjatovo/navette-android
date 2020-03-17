package com.joelinjatovo.navette.database.task;

import android.os.AsyncTask;

import com.joelinjatovo.navette.database.callback.FindCallback;
import com.joelinjatovo.navette.database.dao.UserDao;
import com.joelinjatovo.navette.database.entity.User;

import java.util.List;

public class UserFindAsyncTask extends AsyncTask<Long, Void, List<User>> {

    private static final String TAG = UserFindAsyncTask.class.getSimpleName();

    UserDao dao;

    FindCallback<User> callback;

    public UserFindAsyncTask(UserDao dao, FindCallback<User> callback){
        this.dao = dao;
        this.callback = callback;
    }

    @Override
    protected  List<User> doInBackground(Long... items) {
        return dao.find(items);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(callback!=null){
            callback.onFindError();
        }
    }

    @Override
    protected void onPostExecute(List<User> items) {
        super.onPostExecute(items);
        if(callback!=null){
            if( items != null && !items.isEmpty()){
                callback.onFindSuccess(items);
            }else{
                callback.onFindError();
            }
        }
    }
}

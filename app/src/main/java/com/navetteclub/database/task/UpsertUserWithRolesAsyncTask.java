package com.navetteclub.database.task;

import android.os.AsyncTask;

import com.navetteclub.database.callback.UpsertCallback;
import com.navetteclub.database.dao.UserDao;
import com.navetteclub.database.entity.User;

import java.util.Arrays;
import java.util.List;

public class UpsertUserWithRolesAsyncTask extends AsyncTask<User, Void, List<User>> {

    private static final String TAG = UpsertUserWithRolesAsyncTask.class.getSimpleName();

    UserDao dao;

    UpsertCallback<User> callback;

    public UpsertUserWithRolesAsyncTask(UserDao dao, UpsertCallback<User> callback){
        this.dao = dao;
        this.callback = callback;
    }

    @Override
    protected  List<User> doInBackground(User... items) {
        for(User item: items){
            dao.upsert(item);
        }
        return Arrays.asList(items);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(callback!=null){
            callback.onUpsertError();
        }
    }

    @Override
    protected void onPostExecute(List<User> items) {
        super.onPostExecute(items);
        if(callback!=null){
            if( items != null && !items.isEmpty()){
                callback.onUpsertSuccess(items);
            }else{
                callback.onUpsertError();
            }
        }
    }
}

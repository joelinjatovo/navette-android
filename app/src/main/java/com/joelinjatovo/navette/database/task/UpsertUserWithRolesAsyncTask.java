package com.joelinjatovo.navette.database.task;

import android.os.AsyncTask;

import com.joelinjatovo.navette.database.callback.UpsertCallback;
import com.joelinjatovo.navette.database.dao.UserDao;
import com.joelinjatovo.navette.database.entity.UserWithRoles;

import java.util.Arrays;
import java.util.List;

public class UpsertUserWithRolesAsyncTask extends AsyncTask<UserWithRoles, Void, List<UserWithRoles>> {

    private static final String TAG = UpsertUserWithRolesAsyncTask.class.getSimpleName();

    UserDao dao;

    UpsertCallback<UserWithRoles> callback;

    public UpsertUserWithRolesAsyncTask(UserDao dao, UpsertCallback<UserWithRoles> callback){
        this.dao = dao;
        this.callback = callback;
    }

    @Override
    protected  List<UserWithRoles> doInBackground(UserWithRoles... items) {
        for(UserWithRoles item: items){
            dao.insertUserWithRoles(item.getUser(), item.getRoles());
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
    protected void onPostExecute(List<UserWithRoles> items) {
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

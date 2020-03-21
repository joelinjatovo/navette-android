package com.joelinjatovo.navette.database.task;

import android.os.AsyncTask;

import com.joelinjatovo.navette.database.callback.UpsertCallback;
import com.joelinjatovo.navette.database.dao.BaseDao;
import com.joelinjatovo.navette.utils.Log;

import java.util.List;

public class UpsertAsyncTask<T> extends AsyncTask<T, Void, List<T>> {

    private static final String TAG = UpsertAsyncTask.class.getSimpleName();

    BaseDao<T> dao;

    UpsertCallback<T> callback;

    public UpsertAsyncTask(BaseDao<T> dao, UpsertCallback<T> callback){
        this.dao = dao;
        this.callback = callback;
    }

    @Override
    protected  List<T> doInBackground(T... items) {
        try {
            return dao.upsert(items);
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
            callback.onUpsertError();
        }
    }

    @Override
    protected void onPostExecute(List<T> items) {
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

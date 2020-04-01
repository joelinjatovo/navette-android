package com.navetteclub.database.task;

import android.os.AsyncTask;

import com.navetteclub.database.callback.UpsertCallback;
import com.navetteclub.database.dao.ClubDao;
import com.navetteclub.database.entity.ClubAndPoint;

import java.util.Arrays;
import java.util.List;

public class UpsertClubAndPointAsyncTask extends AsyncTask<ClubAndPoint, Void, List<ClubAndPoint>> {

    private static final String TAG = UpsertClubAndPointAsyncTask.class.getSimpleName();

    private ClubDao dao;

    private UpsertCallback<ClubAndPoint> callback;

    public UpsertClubAndPointAsyncTask(ClubDao dao, UpsertCallback<ClubAndPoint> callback){
        this.dao = dao;
        this.callback = callback;
    }

    @Override
    protected  List<ClubAndPoint> doInBackground(ClubAndPoint... items) {
        for(ClubAndPoint item: items){
            dao.insertClubAndPoint(item.getClub(), item.getPoint());
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
    protected void onPostExecute(List<ClubAndPoint> items) {
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

package com.navetteclub.database.task;

import android.os.AsyncTask;

import com.navetteclub.database.callback.UpsertCallback;
import com.navetteclub.database.dao.ClubDao;
import com.navetteclub.database.entity.Club;

import java.util.Arrays;
import java.util.List;

public class UpsertClubAndPointAsyncTask extends AsyncTask<Club, Void, List<Club>> {

    private static final String TAG = UpsertClubAndPointAsyncTask.class.getSimpleName();

    private ClubDao dao;

    private UpsertCallback<Club> callback;

    public UpsertClubAndPointAsyncTask(ClubDao dao, UpsertCallback<Club> callback){
        this.dao = dao;
        this.callback = callback;
    }

    @Override
    protected  List<Club> doInBackground(Club... items) {
        for(Club item: items){
            dao.insertClubAndPoint(item, item.getPoint());
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
    protected void onPostExecute(List<Club> items) {
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

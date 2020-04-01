package com.navetteclub.database.task;

import android.os.AsyncTask;

import com.navetteclub.database.callback.UpsertCallback;
import com.navetteclub.database.dao.CarDao;
import com.navetteclub.database.dao.ClubDao;
import com.navetteclub.database.entity.CarAndModel;
import com.navetteclub.database.entity.Club;

import java.util.Arrays;
import java.util.List;

public class UpsertCarAndModelAsyncTask extends AsyncTask<CarAndModel, Void, List<CarAndModel>> {

    private static final String TAG = UpsertCarAndModelAsyncTask.class.getSimpleName();

    private Club club;

    private CarDao dao;

    private UpsertCallback<CarAndModel> callback;

    public UpsertCarAndModelAsyncTask(CarDao dao, UpsertCallback<CarAndModel> callback, Club club){
        this.dao = dao;
        this.callback = callback;
        this.club = club;
    }

    @Override
    protected  List<CarAndModel> doInBackground(CarAndModel... items) {
        for(CarAndModel item: items){
            dao.insertCarAndModel(club, item.getCar(), item.getModel());
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
    protected void onPostExecute(List<CarAndModel> items) {
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

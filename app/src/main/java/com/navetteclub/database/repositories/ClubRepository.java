package com.navetteclub.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.navetteclub.database.AppDatabase;
import com.navetteclub.database.callback.UpsertCallback;
import com.navetteclub.database.dao.ClubDao;
import com.navetteclub.database.task.UpsertClubAndPointAsyncTask;

import java.util.List;

public class ClubRepository {

    private static ClubRepository instance;

    private ClubDao clubDao;

    public ClubRepository(Application application){
        AppDatabase database = AppDatabase.getInstance(application);
        clubDao = database.clubDao();
    }

    public static synchronized ClubRepository getInstance(Application application) {
        if(instance == null){
            instance = new ClubRepository(application);
        }
        return instance;
    }

    public LiveData<List<ClubAndPoint>> getList(){
        return clubDao.loadPointAndClub();
    }

    public LiveData<List<ClubAndPoint>> search(String search){
        return clubDao.searchPointAndClub(search);
    }

    /*
    public void upsert(UpsertCallback<Club> callback, Club... items){
        (new UpsertAsyncTask<Club>(clubDao, callback)).execute(items);
    }
     */

    public void upsert(UpsertCallback<ClubAndPoint> callback, ClubAndPoint... items){
        (new UpsertClubAndPointAsyncTask(clubDao, callback)).execute(items);
    }
}

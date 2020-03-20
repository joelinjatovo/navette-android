package com.joelinjatovo.navette.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.joelinjatovo.navette.database.AppDatabase;
import com.joelinjatovo.navette.database.callback.UpsertCallback;
import com.joelinjatovo.navette.database.dao.ClubDao;
import com.joelinjatovo.navette.database.entity.Club;
import com.joelinjatovo.navette.database.entity.ClubAndPoint;
import com.joelinjatovo.navette.database.task.UpsertAsyncTask;

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

    public void upsert(UpsertCallback<Club> callback, Club... items){
        (new UpsertAsyncTask<Club>(clubDao, callback)).execute(items);
    }
}

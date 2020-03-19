package com.joelinjatovo.navette.database.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.joelinjatovo.navette.database.AppDatabase;
import com.joelinjatovo.navette.database.callback.FindCallback;
import com.joelinjatovo.navette.database.callback.UpsertCallback;
import com.joelinjatovo.navette.database.dao.ClubDao;
import com.joelinjatovo.navette.database.dao.UserDao;
import com.joelinjatovo.navette.database.entity.Club;
import com.joelinjatovo.navette.database.entity.User;
import com.joelinjatovo.navette.database.task.UpsertAsyncTask;
import com.joelinjatovo.navette.database.task.UserFindAsyncTask;

import java.util.List;

public class ClubRepository {

    private static ClubRepository instance;

    private ClubDao clubDao;

    private LiveData<List<Club>> listLiveData;

    public ClubRepository(Application application){
        AppDatabase database = AppDatabase.getInstance(application);
        clubDao = database.clubDao();
        listLiveData = clubDao.load();
    }

    public static synchronized ClubRepository getInstance(Application application) {
        if(instance == null){
            instance = new ClubRepository(application);
        }
        return instance;
    }

    public LiveData<List<Club>> getList(){
        return listLiveData;
    }

    public void insert(UpsertCallback<Club> callback, Club... items){
        (new UpsertAsyncTask(clubDao, callback)).execute(items);
    }
}

package com.joelinjatovo.navette.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.joelinjatovo.navette.database.AppDatabase;
import com.joelinjatovo.navette.database.callback.UpsertCallback;
import com.joelinjatovo.navette.database.dao.CarDao;
import com.joelinjatovo.navette.database.dao.ClubDao;
import com.joelinjatovo.navette.database.entity.CarAndModel;
import com.joelinjatovo.navette.database.entity.Club;
import com.joelinjatovo.navette.database.entity.ClubAndPoint;
import com.joelinjatovo.navette.database.task.UpsertCarAndModelAsyncTask;
import com.joelinjatovo.navette.database.task.UpsertClubAndPointAsyncTask;

import java.util.List;

public class CarRepository {

    private static CarRepository instance;

    private CarDao carDao;

    public CarRepository(Application application){
        AppDatabase database = AppDatabase.getInstance(application);
        carDao = database.carDao();
    }

    public static synchronized CarRepository getInstance(Application application) {
        if(instance == null){
            instance = new CarRepository(application);
        }
        return instance;
    }

    public LiveData<List<CarAndModel>> getList(Club club){
        return carDao.loadCarAndClub(club.getId());
    }

    public void upsert(Club club, UpsertCallback<CarAndModel> callback, CarAndModel... items){
        (new UpsertCarAndModelAsyncTask(carDao, callback, club)).execute(items);
    }
}

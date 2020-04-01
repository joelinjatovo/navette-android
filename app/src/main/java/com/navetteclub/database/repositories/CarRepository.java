package com.navetteclub.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.navetteclub.database.AppDatabase;
import com.navetteclub.database.callback.UpsertCallback;
import com.navetteclub.database.dao.CarDao;
import com.navetteclub.database.dao.ClubDao;
import com.navetteclub.database.entity.CarAndModel;
import com.navetteclub.database.entity.Club;
import com.navetteclub.database.task.UpsertCarAndModelAsyncTask;

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

package com.navetteclub.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.navetteclub.database.AppDatabase;
import com.navetteclub.database.callback.UpsertCallback;
import com.navetteclub.database.dao.ClubDao;
import com.navetteclub.database.dao.LocationDao;
import com.navetteclub.database.entity.ClubAndPoint;
import com.navetteclub.database.entity.Location;
import com.navetteclub.database.task.UpsertAsyncTask;
import com.navetteclub.database.task.UpsertClubAndPointAsyncTask;

import java.util.List;

public class LocationRepository {

    private static LocationRepository instance;

    private LocationDao locationDao;

    public LocationRepository(Application application){
        AppDatabase database = AppDatabase.getInstance(application);
        locationDao = database.locationDao();
    }

    public static synchronized LocationRepository getInstance(Application application) {
        if(instance == null){
            instance = new LocationRepository(application);
        }
        return instance;
    }

    public LiveData<List<Location>> getList(){
        return locationDao.load();
    }

    public LiveData<List<Location>> getList(String... types){
        return locationDao.loadByType(types);
    }

    public void delete(Location location){
        locationDao.delete(location);
    }

    public void deleteByType(String... types){
        locationDao.deleteByType(types);
    }

    public void upsert(UpsertCallback<Location> callback, Location... items){
        (new UpsertAsyncTask<Location>(locationDao, callback)).execute(items);
    }

}

package com.navetteclub.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.navetteclub.database.AppDatabase;
import com.navetteclub.database.dao.NotificationDao;
import com.navetteclub.database.entity.Notification;

import java.util.List;

public class NotificationRepository {

    private static NotificationRepository instance;

    private NotificationDao notificationDao;

    public NotificationRepository(Application application){
        AppDatabase database = AppDatabase.getInstance(application);
        notificationDao = database.notificationDao();
    }

    public static synchronized NotificationRepository getInstance(Application application) {
        if(instance == null){
            instance = new NotificationRepository(application);
        }
        return instance;
    }

    public LiveData<List<Notification>> getList(){
        return notificationDao.load();
    }

}

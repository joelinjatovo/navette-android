package com.joelinjatovo.navette.database.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.joelinjatovo.navette.database.AppDatabase;
import com.joelinjatovo.navette.database.callback.UpsertCallback;
import com.joelinjatovo.navette.database.dao.ClubDao;
import com.joelinjatovo.navette.database.dao.NotificationDao;
import com.joelinjatovo.navette.database.entity.ClubAndPoint;
import com.joelinjatovo.navette.database.entity.Notification;
import com.joelinjatovo.navette.database.task.UpsertClubAndPointAsyncTask;

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

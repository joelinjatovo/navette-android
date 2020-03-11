package com.joelinjatovo.navette.app;

import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.joelinjatovo.navette.app.converter.ArrayConverter;
import com.joelinjatovo.navette.app.converter.DateConverter;
import com.joelinjatovo.navette.app.dao.UserDao;
import com.joelinjatovo.navette.app.entity.User;

@Database(entities = {
        User.class
},version = 1)
@TypeConverters({DateConverter.class, ArrayConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;

    @VisibleForTesting
    private static final String DATABASE_NAME = "navette-db";

    public abstract UserDao userDao();

    public static AppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME).build();
                }
            }
        }
        return sInstance;
    }
}

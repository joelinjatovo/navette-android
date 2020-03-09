package com.joelinjatovo.navette.app;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.joelinjatovo.navette.app.converter.ArrayConverter;
import com.joelinjatovo.navette.app.converter.DateConverter;
import com.joelinjatovo.navette.app.entity.User;

@Database(entities = {
        User.class
},version = 1)
@TypeConverters({DateConverter.class, ArrayConverter.class})
public abstract class AppDatabase extends RoomDatabase {
}

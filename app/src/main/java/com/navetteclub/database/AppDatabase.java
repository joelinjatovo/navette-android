package com.navetteclub.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.navetteclub.database.converter.ArrayConverter;
import com.navetteclub.database.converter.DateConverter;
import com.navetteclub.database.converter.ObjectConverter;
import com.navetteclub.database.dao.CarDao;
import com.navetteclub.database.dao.ClubDao;
import com.navetteclub.database.dao.NotificationDao;
import com.navetteclub.database.dao.OrderPointDao;
import com.navetteclub.database.dao.PointDao;
import com.navetteclub.database.dao.UserDao;
import com.navetteclub.database.entity.Car;
import com.navetteclub.database.entity.CarModel;
import com.navetteclub.database.entity.Club;
import com.navetteclub.database.entity.Notification;
import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderPoint;
import com.navetteclub.database.entity.Point;
import com.navetteclub.database.entity.Ride;
import com.navetteclub.database.entity.RidePoint;
import com.navetteclub.database.entity.User;
import com.navetteclub.database.entity.UserPoint;

@Database(entities = {
        Car.class,
        CarModel.class,
        Club.class,
        Notification.class,
        Order.class,
        OrderPoint.class,
        Point.class,
        Ride.class,
        RidePoint.class,
        User.class,
        UserPoint.class,
},version = 1, exportSchema = false)
@TypeConverters({DateConverter.class, ArrayConverter.class, ObjectConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;

    @VisibleForTesting
    private static final String DATABASE_NAME = "navette-db";

    public abstract CarDao carDao();

    public abstract ClubDao clubDao();

    public abstract NotificationDao notificationDao();

    public abstract OrderPointDao orderPointDao();

    public abstract PointDao pointDao();

    public abstract UserDao userDao();

    public static AppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                            .addMigrations(MIGRATION_1_2)
                            .build();
                }
            }
        }
        return sInstance;
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            /*
            database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `productsFts` USING FTS4("
                    + "`name` TEXT, `description` TEXT, content=`products`)");
            database.execSQL("INSERT INTO productsFts (`rowid`, `name`, `description`) "
                    + "SELECT `id`, `name`, `description` FROM products");
            */

        }
    };

}

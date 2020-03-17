package com.joelinjatovo.navette.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.SkipQueryVerification;
import androidx.room.Transaction;
import androidx.room.Update;

import com.joelinjatovo.navette.database.entity.User;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract Long insert(T item);

    @Update
    public abstract int update(T item);

    @Delete
    public abstract int delete(T item);

    @Transaction
    public abstract List<T> upsert(T... items);
}

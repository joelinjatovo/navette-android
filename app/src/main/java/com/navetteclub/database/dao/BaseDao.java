package com.navetteclub.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Transaction;
import androidx.room.Update;

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

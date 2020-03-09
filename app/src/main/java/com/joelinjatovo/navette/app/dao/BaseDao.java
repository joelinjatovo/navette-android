package com.joelinjatovo.navette.app.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

public interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long[] insert(T... entities);

    @Update
    Long update(T... entities);

    @Delete
    Long delete(T... entities);
}

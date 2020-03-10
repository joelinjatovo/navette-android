package com.joelinjatovo.navette.app.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.joelinjatovo.navette.app.entity.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long[] insert(User... entities);

    @Update
    int update(User... entities);

    @Delete
    int delete(User... entities);

    @Query("SELECT * FROM users")
    List<User> find();

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    User find(Integer id);

    @Query("SELECT * FROM users WHERE id IN (:ids)")
    User find(int[] ids);

    @Query("SELECT * FROM users")
    LiveData<List<User>> load();

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    LiveData<User> load(int id);

    @Query("SELECT * FROM users WHERE id IN (:ids)")
    LiveData<List<User>> load(int[] ids);

    @Query("SELECT COUNT(users.id) FROM users")
    Integer count();
}

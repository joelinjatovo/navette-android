package com.joelinjatovo.navette.app.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import com.joelinjatovo.navette.app.entity.User;

import java.util.List;

@Dao
public abstract class UserDao implements BaseDao<User> {
    @Query("SELECT * FROM users")
    abstract List<User> find();

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    abstract User find(Integer id);

    @Query("SELECT * FROM users WHERE id IN (:ids)")
    abstract User find(int[] ids);

    @Query("SELECT * FROM users")
    abstract LiveData<List<User>> load();

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    abstract LiveData<User> load(int id);

    @Query("SELECT * FROM users WHERE id IN (:ids)")
    abstract LiveData<List<User>> load(int[] ids);

    @Query("SELECT COUNT(users.id) FROM users")
    abstract Integer count();
}

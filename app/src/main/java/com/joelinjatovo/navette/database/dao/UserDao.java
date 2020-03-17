package com.joelinjatovo.navette.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.joelinjatovo.navette.database.entity.User;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class UserDao extends BaseDao<User> {

    @Query("SELECT * FROM users")
    public abstract List<User> find();

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    public abstract User find(Long id);

    @Query("SELECT * FROM users WHERE id IN (:ids)")
    public abstract User[] find(Long[] ids);

    @Query("SELECT * FROM users")
    public abstract LiveData<List<User>> load();

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    public abstract LiveData<User> load(Long id);

    @Query("SELECT * FROM users WHERE id IN (:ids)")
    public abstract LiveData<List<User>> load(Long[] ids);

    @Query("SELECT COUNT(users.id) FROM users")
    public abstract int count();

    @Transaction
    public List<User> upsert(User... users) {
        List<User> output = new ArrayList<>(users.length);
        for(User user: users){
            Long id = insert(user);
            if(id == -1) {
                int res = update(user);
                if(res > 0 ) {
                    output.add(find(user.getId()));
                }
            }else{
                output.add(find(user.getId()));
            }
        }
        return output;
    }
}

package com.joelinjatovo.navette.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.joelinjatovo.navette.database.entity.Club;
import com.joelinjatovo.navette.database.entity.ClubAndPoint;
import com.joelinjatovo.navette.database.entity.Notification;
import com.joelinjatovo.navette.database.entity.Point;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class NotificationDao extends BaseDao<Notification> {

    @Query("SELECT * FROM clubs")
    public abstract List<Notification> find();

    @Query("SELECT * FROM clubs WHERE id = :id LIMIT 1")
    public abstract Notification find(Long id);

    @Query("SELECT * FROM clubs WHERE id IN (:ids)")
    public abstract List<Notification> find(Long... ids);

    @Query("SELECT * FROM clubs")
    public abstract LiveData<List<Notification>> load();

    @Query("SELECT * FROM clubs WHERE id = :id LIMIT 1")
    public abstract LiveData<Notification> load(Long id);

    @Query("SELECT * FROM clubs WHERE id IN (:ids)")
    public abstract LiveData<List<Notification>> load(Long[] ids);

    @Query("SELECT COUNT(clubs.id) FROM clubs")
    public abstract int count();

    @Transaction
    public List<Notification> upsert(Notification... items) {
        List<Notification> output = new ArrayList<>(items.length);
        for(Notification item: items){
            Long id = insert(item);
            if(id == -1) {
                int res = update(item);
                if(res > 0 ) {
                    output.add(item);
                }
            }else{
                output.add(item);
            }
        }
        return output;
    }
}

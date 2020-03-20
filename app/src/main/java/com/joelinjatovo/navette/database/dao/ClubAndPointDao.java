package com.joelinjatovo.navette.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.joelinjatovo.navette.database.entity.ClubAndPoint;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class ClubAndPointDao extends BaseDao<ClubAndPoint> {

    @Transaction
    @Query("SELECT * FROM clubs")
    public abstract List<ClubAndPoint> find();

    @Transaction
    @Query("SELECT * FROM clubs WHERE id = :id LIMIT 1")
    public abstract ClubAndPoint find(Long id);

    @Transaction
    @Query("SELECT * FROM clubs WHERE id IN (:ids)")
    public abstract List<ClubAndPoint> find(Long... ids);

    @Transaction
    @Query("SELECT * FROM clubs")
    public abstract LiveData<List<ClubAndPoint>> load();

    @Transaction
    @Query("SELECT * FROM clubs WHERE id = :id LIMIT 1")
    public abstract LiveData<ClubAndPoint> load(Long id);

    @Transaction
    @Query("SELECT * FROM clubs WHERE id IN (:ids)")
    public abstract LiveData<List<ClubAndPoint>> load(Long[] ids);

    @Transaction
    @Query("SELECT COUNT(clubs.id) FROM clubs")
    public abstract int count();

    @Transaction
    public List<ClubAndPoint> upsert(ClubAndPoint... items) {
        List<ClubAndPoint> output = new ArrayList<>(items.length);
        for(ClubAndPoint item: items){
            Long id = insert(item);
            if(id == -1) {
                int res = update(item);
                if(res > 0 ) {
                    output.add(find(item.getClub().getId()));
                }
            }else{
                output.add(find(item.getClub().getId()));
            }
        }
        return output;
    }
}

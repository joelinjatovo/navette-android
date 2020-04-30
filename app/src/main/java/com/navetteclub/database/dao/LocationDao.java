package com.navetteclub.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.navetteclub.database.entity.Location;
import com.navetteclub.database.entity.Point;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class LocationDao extends BaseDao<Location> {

    @Query("SELECT * FROM locations")
    public abstract List<Location> find();

    @Query("SELECT * FROM locations WHERE id = :id LIMIT 1")
    public abstract Location find(String id);

    @Query("SELECT * FROM locations WHERE id IN (:ids)")
    public abstract List<Location> find(String... ids);

    @Query("SELECT * FROM locations")
    public abstract LiveData<List<Location>> load();

    @Query("SELECT * FROM locations WHERE id = :id LIMIT 1")
    public abstract LiveData<Location> load(String id);

    @Query("SELECT * FROM locations WHERE id IN (:ids)")
    public abstract LiveData<List<Location>> load(String[] ids);

    @Query("SELECT * FROM locations WHERE type IN (:types)")
    public abstract LiveData<List<Location>> loadByType(String... types);

    @Query("SELECT COUNT(locations.id) FROM locations")
    public abstract int count();

    @Transaction
    public List<Location> upsert(Location... items) {
        List<Location> output = new ArrayList<>(items.length);
        for(Location item: items){
            Long id = insert(item);
            if(id == -1) {
                int res = update(item);
                if(res > 0 ) {
                    output.add(find(item.getId()));
                }
            }else{
                output.add(find(item.getId()));
            }
        }
        return output;
    }
}

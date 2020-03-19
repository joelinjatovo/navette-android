package com.joelinjatovo.navette.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.joelinjatovo.navette.database.entity.Point;
import com.joelinjatovo.navette.database.entity.User;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class PointDao extends BaseDao<Point> {

    @Query("SELECT * FROM points")
    public abstract List<Point> find();

    @Query("SELECT * FROM points WHERE id = :id LIMIT 1")
    public abstract Point find(Long id);

    @Query("SELECT * FROM points WHERE id IN (:ids)")
    public abstract List<Point> find(Long... ids);

    @Query("SELECT * FROM points")
    public abstract LiveData<List<Point>> load();

    @Query("SELECT * FROM points WHERE id = :id LIMIT 1")
    public abstract LiveData<Point> load(Long id);

    @Query("SELECT * FROM points WHERE id IN (:ids)")
    public abstract LiveData<List<Point>> load(Long[] ids);

    @Query("SELECT COUNT(points.id) FROM users")
    public abstract int count();

    @Transaction
    public List<Point> upsert(Point... points) {
        List<Point> output = new ArrayList<>(points.length);
        for(Point point: points){
            Long id = insert(point);
            if(id == -1) {
                int res = update(point);
                if(res > 0 ) {
                    output.add(find(point.getId()));
                }
            }else{
                output.add(find(point.getId()));
            }
        }
        return output;
    }
}

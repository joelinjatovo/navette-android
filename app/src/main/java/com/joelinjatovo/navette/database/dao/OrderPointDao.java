package com.joelinjatovo.navette.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.joelinjatovo.navette.database.entity.OrderPoint;
import com.joelinjatovo.navette.database.entity.User;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class OrderPointDao extends BaseDao<OrderPoint> {

    @Query("SELECT * FROM order_point")
    public abstract List<OrderPoint> find();

    @Query("SELECT * FROM order_point WHERE id = :id LIMIT 1")
    public abstract OrderPoint find(Long id);

    @Query("SELECT * FROM order_point WHERE id IN (:ids)")
    public abstract List<OrderPoint> find(Long... ids);

    @Query("SELECT * FROM order_point")
    public abstract LiveData<List<OrderPoint>> load();

    @Query("SELECT * FROM order_point WHERE id = :id LIMIT 1")
    public abstract LiveData<OrderPoint> load(Long id);

    @Query("SELECT * FROM order_point WHERE id IN (:ids)")
    public abstract LiveData<List<OrderPoint>> load(Long[] ids);

    @Query("SELECT COUNT(order_point.id) FROM users")
    public abstract int count();

    @Transaction
    public List<OrderPoint> upsert(OrderPoint... items) {
        List<OrderPoint> output = new ArrayList<>(items.length);
        for(OrderPoint item: items){
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

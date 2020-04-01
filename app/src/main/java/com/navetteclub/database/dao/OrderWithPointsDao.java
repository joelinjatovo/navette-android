package com.navetteclub.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.navetteclub.database.entity.OrderWithPoints;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class OrderWithPointsDao extends BaseDao<OrderWithPoints> {

    @Transaction
    @Query("SELECT * FROM orders")
    public abstract List<OrderWithPoints> find();

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    public abstract OrderWithPoints find(Long id);

    @Transaction
    @Query("SELECT * FROM orders WHERE id IN (:ids)")
    public abstract List<OrderWithPoints> find(Long... ids);

    @Transaction
    @Query("SELECT * FROM orders")
    public abstract LiveData<List<OrderWithPoints>> load();

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    public abstract LiveData<OrderWithPoints> load(Long id);

    @Transaction
    @Query("SELECT * FROM orders WHERE id IN (:ids)")
    public abstract LiveData<List<OrderWithPoints>> load(Long[] ids);

    @Transaction
    @Query("SELECT COUNT(orders.id) FROM orders")
    public abstract int count();

    @Transaction
    public List<OrderWithPoints> upsert(OrderWithPoints... items) {
        List<OrderWithPoints> output = new ArrayList<>(items.length);
        for(OrderWithPoints item: items){
            Long id = insert(item);
            if(id == -1) {
                int res = update(item);
                if(res > 0 ) {
                    output.add(find(item.getOrder().getId()));
                }
            }else{
                output.add(find(item.getOrder().getId()));
            }
        }
        return output;
    }
}

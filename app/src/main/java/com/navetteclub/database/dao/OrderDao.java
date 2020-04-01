package com.navetteclub.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.navetteclub.database.entity.Order;
import com.navetteclub.database.entity.OrderWithPoints;
import com.navetteclub.database.entity.Point;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class OrderDao extends BaseDao<Order> {

    @Query("SELECT * FROM orders")
    public abstract List<Order> find();

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    public abstract Order find(Long id);

    @Query("SELECT * FROM orders WHERE id IN (:ids)")
    public abstract List<Order> find(Long... ids);

    @Query("SELECT * FROM orders")
    public abstract LiveData<List<Order>> load();

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    public abstract LiveData<Order> load(Long id);

    @Query("SELECT * FROM orders WHERE id IN (:ids)")
    public abstract LiveData<List<Order>> load(Long[] ids);

    @Query("SELECT COUNT(orders.id) FROM orders")
    public abstract int count();

    @Transaction
    public List<Order> upsert(Order... orders) {
        List<Order> output = new ArrayList<>(orders.length);
        for(Order order: orders){
            Long id = insert(order);
            if(id == -1) {
                int res = update(order);
                if(res > 0 ) {
                    output.add(find(order.getId()));
                }
            }else{
                output.add(find(order.getId()));
            }
        }
        return output;
    }

    @Transaction
    @Query("SELECT * FROM orders")
    public abstract List<OrderWithPoints> findOrdersWithPoints();

    @Transaction
    @Query("SELECT * FROM orders")
    public abstract LiveData<List<OrderWithPoints>> loadOrdersWithPoints();
}

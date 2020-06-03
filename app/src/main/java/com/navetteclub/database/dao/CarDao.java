package com.navetteclub.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.navetteclub.database.entity.Car;
import com.navetteclub.database.entity.CarModel;
import com.navetteclub.database.entity.Club;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class CarDao extends BaseDao<Car> {

    @Query("SELECT * FROM cars")
    public abstract List<Car> find();

    @Query("SELECT * FROM cars WHERE id = :id LIMIT 1")
    public abstract Car find(Long id);

    @Query("SELECT * FROM cars WHERE id IN (:ids)")
    public abstract List<Car> find(Long... ids);

    @Query("SELECT * FROM cars")
    public abstract LiveData<List<Car>> load();

    @Query("SELECT * FROM cars WHERE id = :id LIMIT 1")
    public abstract LiveData<Car> load(Long id);

    @Query("SELECT * FROM cars WHERE id IN (:ids)")
    public abstract LiveData<List<Car>> load(Long[] ids);

    @Query("SELECT COUNT(cars.id) FROM cars")
    public abstract int count();

    @Transaction
    public List<Car> upsert(Car... items) {
        List<Car> output = new ArrayList<>(items.length);
        for(Car item: items){
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

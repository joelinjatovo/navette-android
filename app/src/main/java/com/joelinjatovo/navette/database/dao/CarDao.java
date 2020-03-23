package com.joelinjatovo.navette.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.joelinjatovo.navette.database.entity.Car;
import com.joelinjatovo.navette.database.entity.CarAndModel;
import com.joelinjatovo.navette.database.entity.CarModel;
import com.joelinjatovo.navette.database.entity.Club;

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

    @Transaction
    public void insertCarAndModel(Club club, Car car, CarModel model) {
        _insertCarModel(model);
        car.setCarModelId(model.getId());
        car.setClubId(club.getId());
        insert(car);
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract Long _insertCarModel(CarModel model);

    @Query("SELECT * FROM car_models JOIN cars ON cars.car_model_id = car_models.id WHERE cars.club_id = :clubId")
    public abstract LiveData<List<CarAndModel>> loadCarAndClub(Long clubId);
}

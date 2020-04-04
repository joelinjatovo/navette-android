package com.navetteclub.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.navetteclub.database.entity.Club;
import com.navetteclub.database.entity.ClubAndPoint;
import com.navetteclub.database.entity.Point;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class ClubDao extends BaseDao<Club> {

    @Query("SELECT * FROM clubs")
    public abstract List<Club> find();

    @Query("SELECT * FROM clubs WHERE id = :id LIMIT 1")
    public abstract Club find(Long id);

    @Query("SELECT * FROM clubs WHERE id IN (:ids)")
    public abstract List<Club> find(Long... ids);

    @Query("SELECT * FROM clubs")
    public abstract LiveData<List<Club>> load();

    @Query("SELECT * FROM clubs WHERE id = :id LIMIT 1")
    public abstract LiveData<Club> load(Long id);

    @Query("SELECT * FROM clubs WHERE id IN (:ids)")
    public abstract LiveData<List<Club>> load(Long[] ids);

    @Query("SELECT COUNT(clubs.id) FROM clubs")
    public abstract int count();

    @Transaction
    public List<Club> upsert(Club... clubs) {
        List<Club> output = new ArrayList<>(clubs.length);
        for(Club club: clubs){
            Long id = insert(club);
            if(id == -1) {
                int res = update(club);
                if(res > 0 ) {
                    output.add(find(club.getId()));
                }
            }else{
                output.add(find(club.getId()));
            }
        }
        return output;
    }

    @Transaction
    @Query("SELECT * FROM points JOIN clubs ON clubs.point_id = points.id WHERE clubs.name LIKE :search")
    public abstract LiveData<List<ClubAndPoint>> searchPointAndClub(String search);

    @Transaction
    @Query("SELECT * FROM points")
    public abstract LiveData<List<ClubAndPoint>> loadPointAndClub();

    @Transaction
    public void insertClubAndPoint(Club club, Point point) {
        _insertPoint(point);
        club.setPointId(point.getId());
        Long clubId = insert(club);
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void _insertPoint(Point point);
}

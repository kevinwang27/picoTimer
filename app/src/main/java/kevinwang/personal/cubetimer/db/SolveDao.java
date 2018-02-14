package kevinwang.personal.cubetimer.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import kevinwang.personal.cubetimer.db.entity.Solve;

/**
 * Created by kevinwang on 2/9/18.
 */

@Dao
public interface SolveDao {
    @Insert
    public long insertSolve(Solve solve);

    @Delete
    public void deleteSolve(Solve solve);

    @Query("DELETE FROM solve WHERE solve_time = :time")
    public void deleteSolveByTime(String time);

    @Query("SELECT * FROM solve")
    public List<Solve> loadAllSolves();

/*    @Query("SELECT * FROM solve WHERE solveNum > max(solveNum)-:count")
    public Solve[] loadRecentSolves(int count);*/

    @Query("SELECT min(solve_time) FROM solve")
    public String loadFastestSolve();


}

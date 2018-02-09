package kevinwang.personal.cubetimer.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

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

    @Query("SELECT * FROM solve")
    public Solve[] loadAllSolves();

/*    @Query("SELECT * FROM solve WHERE solveNum > max(solveNum)-:count")
    public Solve[] loadRecentSolves(int count);

    @Query("SELECT * FROM solve WHERE solve_time = min(solve_time) LIMIT 1")
    public String loadFastestSolve();*/


}

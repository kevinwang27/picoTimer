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

    @Query("DELETE FROM solve")
    public void clearSolves();

    @Query("UPDATE solve SET solve_time = :time WHERE scramble = :scramble")
    public void setSolveTimeByScramble(String scramble, String time);

    @Query("UPDATE solve SET oldTime = :old_time WHERE scramble = :scramble")
    public void setOldTimeByScramble(String scramble, String old_time);

    @Query("SELECT * FROM solve WHERE scramble = :scramble LIMIT 1")
    public Solve getSolveByScramble(String scramble);

    @Query("DELETE FROM solve WHERE scramble = :scramble")
    public void deleteSolveByScramble(String scramble);

    @Query("SELECT * FROM solve")
    public List<Solve> loadAllSolves();
}

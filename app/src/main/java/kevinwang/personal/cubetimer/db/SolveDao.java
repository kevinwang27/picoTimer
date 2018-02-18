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

    @Query("DELETE FROM solve")
    public void clearSolves();

    @Query("DELETE FROM solve WHERE session = :session")
    public void clearSolvesBySession(String session);

    @Query("UPDATE solve SET solve_time = :time WHERE scramble = :scramble")
    public void setSolveTimeByScramble(String scramble, String time);

    @Query("DELETE FROM solve WHERE scramble = :scramble")
    public void deleteSolveByScramble(String scramble);

    @Query("SELECT * FROM solve")
    public List<Solve> loadAllSolves();

    @Query("SELECT * FROM solve WHERE session = :session")
    public List<Solve> loadAllSolvesBySession(String session);
}

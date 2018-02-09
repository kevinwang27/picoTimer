package kevinwang.personal.cubetimer.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import kevinwang.personal.cubetimer.db.entity.Solve;

/**
 * Created by kevinwang on 2/9/18.
 */

@Database(entities = {Solve.class}, version = 1)
public abstract class SolveDatabase extends RoomDatabase {
    public abstract SolveDao solveDao();
}


package kevinwang.personal.cubetimer.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by kevinwang on 2/9/18.
 */

@Entity
public class Solve {
    @PrimaryKey(autoGenerate = true)
    private int solveID;

    @ColumnInfo(name = "solve_time")
    private String solveTime;

    private String oldTime;

    private String scramble;

    public int getSolveID() {
        return solveID;
    }

    public void setSolveID(int solveID) {
        this.solveID = solveID;
    }

    public String getSolveTime() {
        return solveTime;
    }

    public void setSolveTime(String solveTime) {
        this.solveTime = solveTime;
    }

    public String getScramble() {
        return scramble;
    }

    public void setScramble(String scramble) {
        this.scramble = scramble;
    }

    public String getOldTime() {
        return oldTime;
    }

    public void setOldTime(String oldTime) {
        this.oldTime = oldTime;
    }
}

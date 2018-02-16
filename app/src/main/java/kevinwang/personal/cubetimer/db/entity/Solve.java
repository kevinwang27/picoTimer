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
    private int solve_num;

    @ColumnInfo(name = "solve_time")
    private String solveTime;

    private String oldTime;

    private String scramble;

    public int getSolve_num() {
        return solve_num;
    }

    public void setSolve_num(int solve_num) {
        this.solve_num = solve_num;
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

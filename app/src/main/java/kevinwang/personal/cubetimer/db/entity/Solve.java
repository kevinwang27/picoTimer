package kevinwang.personal.cubetimer.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by kevinwang on 2/9/18.
 */

@Entity
public class Solve {
    @PrimaryKey
    private int solveNum;

    @ColumnInfo(name = "solve_time")
    private String solveTime;

    private String scramble;


    public int getSolveNum() {
        return solveNum;
    }

    public void setSolveNum(int solveNum) {
        this.solveNum = solveNum;
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
}

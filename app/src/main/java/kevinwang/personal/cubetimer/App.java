package kevinwang.personal.cubetimer;

import android.app.Application;
import android.arch.persistence.room.Room;

import kevinwang.personal.cubetimer.db.SolveDatabase;

/**
 * Created by kevinwang on 2/9/18.
 */

public class App extends Application {

    public static App INSTANCE;
    private static final String DATABASE_NAME = "SolveDatabase";

    private SolveDatabase database;

    public static App get() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        database = Room.databaseBuilder(getApplicationContext(), SolveDatabase.class, DATABASE_NAME).build();

        INSTANCE = this;
    }

    public SolveDatabase getDatabase() {
        return database;
    }
}

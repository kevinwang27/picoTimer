package kevinwang.personal.cubetimer;

import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    View mEntireView;
    TextView mTextTimer;
    Handler customHandler = new Handler();
    TextView mScrambleText;
    BottomNavigationView mBottomNavigationView;
    //TextView mScrambleHead;

    long startTime = 0L, timeInMilli = 0L, timeSwapBuff = 0L, updateTime = 0L;
    boolean time_running;

    Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeInMilli = SystemClock.uptimeMillis() - startTime;
            updateTime = timeSwapBuff + timeInMilli;
            int secs = (int) (updateTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) ((updateTime % 1000) / 10);
            if (mins == 0) {
                mTextTimer.setText("" + String.format("%2d", secs) + ":"
                        + String.format("%02d", milliseconds));
            } else {
                mTextTimer.setText("" + mins + ":" + String.format("%02d", secs) + ":"
                        + String.format("%02d", milliseconds));
            }
            customHandler.postDelayed(this, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        time_running = false;
        mEntireView = findViewById(R.id.entire_view);
        mTextTimer = (TextView) findViewById(R.id.timerValue);
        mScrambleText = (TextView) findViewById(R.id.scramble);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        //mScrambleHead = (TextView) findViewById(R.id.scramble_header);

        if (savedInstanceState == null) {
            mScrambleText.setText(generateScramble());
        } else {
            if (savedInstanceState.getBoolean("scramble_visible")) {
                mScrambleText.setText(savedInstanceState.getString("scramble"));
                mTextTimer.setText(savedInstanceState.getString("time_text"));
            } else {
                startTime = savedInstanceState.getLong("start_time");
                customHandler.postDelayed(updateTimerThread, 0);
                time_running = true;
            }
        }
        mEntireView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getActionMasked();

                if (action == MotionEvent.ACTION_DOWN) {
                    if (time_running == false) {
                        // Indicate holding down
                        mEntireView.setBackgroundColor(getResources().getColor(R.color.darkerBlue));
                        mScrambleText.setVisibility(View.INVISIBLE);
                        mBottomNavigationView.setVisibility(View.INVISIBLE);
                        //mScrambleHead.setVisibility(View.INVISIBLE);

                    } else {
                        // Stop the timer
                        timeSwapBuff = 0;
                        customHandler.removeCallbacks(updateTimerThread);
                        mScrambleText.setText(generateScramble());
                        mScrambleText.setVisibility(View.VISIBLE);
                        mBottomNavigationView.setVisibility(View.VISIBLE);
                        //mScrambleHead.setVisibility(View.VISIBLE);
                    }

                } else if (action == MotionEvent.ACTION_UP) {
                    if (time_running == false) {
                        // Start the timer
                        mEntireView.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimerThread, 0);
                        time_running = true;
                    } else {
                        // Ready the timer for next time
                        time_running = false;
                    }
                }
                return true;
            }
        });
    }

    private String generateScramble() {
        int scrambleLength = 19;

        ArrayList<String> turnsList = new ArrayList<>(Arrays.asList("F", "R", "U", "B", "D", "L"));
        ArrayList<String> scrambleTurns = new ArrayList<>();
        ArrayList<String> turnModifiers = new ArrayList<>(Arrays.asList("\'", "2", ""));

        String lastTurn, currTurn, toInsert, currModifier;
        Random random = new Random();

        for (int i = 0; i < scrambleLength; i++) {
            if (i != 0) {
                lastTurn = scrambleTurns.get(i - 1).split("(?!^)")[0];
            } else {
                lastTurn = null;
            }
            currTurn = turnsList.get(random.nextInt(turnsList.size()));
            while (currTurn.equals(lastTurn)) {
                currTurn = turnsList.get(random.nextInt(turnsList.size()));
            }
            currModifier = turnModifiers.get(random.nextInt(turnModifiers.size()));
            toInsert = currTurn + currModifier;
            scrambleTurns.add(toInsert);
        }
        String returnString = "";

        for (String x : scrambleTurns) {
            returnString += x + "  ";
        }
        return returnString;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mScrambleText.getVisibility() == View.VISIBLE) {
            outState.putBoolean("scramble_visible", true);
            outState.putString("scramble", mScrambleText.getText().toString());
            outState.putString("time_text", mTextTimer.getText().toString());
        } else {
            outState.putLong("start_time", startTime);
        }
    }
}

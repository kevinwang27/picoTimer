package kevinwang.personal.cubetimer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import kevinwang.personal.cubetimer.db.entity.Solve;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {

    public TextView mTextTimer;
    Handler customHandler = new Handler();
    public TextView mScrambleText;
    BottomNavigationView mBottomNavigationView;
    View background;
    SharedPreferences sharedPref;

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
                if (secs >= 10) {
                    mTextTimer.setText("" + String.format("%2d", secs) + ":"
                            + String.format("%02d", milliseconds));
                } else {
                    mTextTimer.setText("" + String.format("%1d", secs) + ":"
                            + String.format("%02d", milliseconds));
                }
            } else {
                mTextTimer.setText("" + mins + ":" + String.format("%02d", secs) + ":"
                        + String.format("%02d", milliseconds));
            }
            customHandler.postDelayed(this, 0);
        }
    };

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /* default timer values */
        mTextTimer.setTextSize(sharedPref.getInt("timer_size", 3) * 8 + 50);
        mScrambleText.setTextSize(sharedPref.getInt("scramble_size", 3) * 2 + 15);
        if (sharedPref.getString("theme", "BLACK").equals("WHITE")) {
            mTextTimer.setTextColor(getResources().getColor(R.color.black));
            mScrambleText.setTextColor(getResources().getColor(R.color.black));
        } else {
            mTextTimer.setTextColor(getResources().getColor(R.color.white));
            mScrambleText.setTextColor(getResources().getColor(R.color.white));
        }


        if (savedInstanceState == null) {
            mScrambleText.setText(sharedPref.getString("current_scramble", generateScramble()));
            if (sharedPref.getBoolean("first_launch", true)) {
                mTextTimer.setText("0:00");
            } else {
                mTextTimer.setText(sharedPref.getString("current_time", "0:00"));
            }
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("current_scramble", mScrambleText.getText().toString()).apply();
        } else {
            if (savedInstanceState.getBoolean("scramble_visible")) {
                mScrambleText.setText(savedInstanceState.getString("scramble"));
                mTextTimer.setText(savedInstanceState.getString("time_text"));
            } else {
                mBottomNavigationView.setVisibility(View.INVISIBLE);
                background.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                mScrambleText.setVisibility(View.INVISIBLE);
                startTime = savedInstanceState.getLong("start_time");
                customHandler.postDelayed(updateTimerThread, 0);
                time_running = true;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private String generateScramble() {
        int scrambleLength = 18;

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

    private void insertSolve() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Solve solve = new Solve();
                solve.setSolveTime(mTextTimer.getText().toString());
                solve.setScramble(mScrambleText.getText().toString());
                solve.setOldTime(mTextTimer.getText().toString());
                solve.setSession(sharedPref.getString("session", "1"));
                App.get().getDatabase().solveDao().insertSolve(solve);
            }
        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        mTextTimer = (TextView) view.findViewById(R.id.timerValue);
        mScrambleText = (TextView) view.findViewById(R.id.scramble);
        mBottomNavigationView = (BottomNavigationView) getActivity().findViewById(R.id.bottom_navigation);
        background = getActivity().findViewById(R.id.container);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        switch (sharedPref.getString("theme", "BLACK")) {
            case "BLACK":
                view.setBackgroundColor(getResources().getColor(R.color.black));
                break;
            case "WHITE":
                view.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case "BLUE":
                view.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
            case "GREEN":
                view.setBackgroundColor(getResources().getColor(R.color.green));
                break;
            case "RED":
                view.setBackgroundColor(getResources().getColor(R.color.red));
                break;
            case "PURPLE":
                view.setBackgroundColor(getResources().getColor(R.color.purple));
                break;
            case "Unicorn":
                view.setBackgroundColor(getResources().getColor(R.color.pink));
                break;
        }

        time_running = false;

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (time_running == false) {
                        // Indicate holding down
                        switch (sharedPref.getString("theme", "BLACK")) {// change to slight color difference
                            case "BLACK":
                                view.setBackgroundColor(getResources().getColor(R.color.darkgray));
                                background.setBackgroundColor(getResources().getColor(R.color.darkgray));
                                break;
                            case "WHITE":
                                view.setBackgroundColor(getResources().getColor(R.color.whitediff));
                                background.setBackgroundColor(getResources().getColor(R.color.whitediff));
                                break;
                            case "BLUE":
                                view.setBackgroundColor(getResources().getColor(R.color.bluediff));
                                background.setBackgroundColor(getResources().getColor(R.color.bluediff));
                                break;
                            case "GREEN":
                                view.setBackgroundColor(getResources().getColor(R.color.greendiff));
                                background.setBackgroundColor(getResources().getColor(R.color.greendiff));
                                break;
                            case "RED":
                                view.setBackgroundColor(getResources().getColor(R.color.reddiff));
                                background.setBackgroundColor(getResources().getColor(R.color.reddiff));
                                break;
                            case "PURPLE":
                                view.setBackgroundColor(getResources().getColor(R.color.purplediff));
                                background.setBackgroundColor(getResources().getColor(R.color.purplediff));
                                break;
                            case "Unicorn":
                                view.setBackgroundColor(getResources().getColor(R.color.unicorndiff));
                                background.setBackgroundColor(getResources().getColor(R.color.unicorndiff));
                                break;
                        }
                        mTextTimer.setText(R.string.start_time);
                        mScrambleText.setVisibility(View.INVISIBLE);
                        mBottomNavigationView.setVisibility(View.INVISIBLE);

                    } else {
                        // Stop the timer
                        timeSwapBuff = 0;
                        customHandler.removeCallbacks(updateTimerThread);

                        insertSolve();

                        mScrambleText.setText(generateScramble());
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("current_time", mTextTimer.getText().toString());
                        editor.putString("current_scramble" , mScrambleText.getText().toString()).apply();
                        mScrambleText.setVisibility(View.VISIBLE);
                        mBottomNavigationView.setVisibility(View.VISIBLE);
                    }

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (time_running == false) {
                        // Start the timer
                        switch (sharedPref.getString("theme", "BLACK")) {
                            case "BLACK":
                                view.setBackgroundColor(getResources().getColor(R.color.black));
                                background.setBackgroundColor(getResources().getColor(R.color.black));
                                break;
                            case "WHITE":
                                view.setBackgroundColor(getResources().getColor(R.color.white));
                                background.setBackgroundColor(getResources().getColor(R.color.white));
                                break;
                            case "BLUE":
                                view.setBackgroundColor(getResources().getColor(R.color.blue));
                                background.setBackgroundColor(getResources().getColor(R.color.blue));
                                break;
                            case "GREEN":
                                view.setBackgroundColor(getResources().getColor(R.color.green));
                                background.setBackgroundColor(getResources().getColor(R.color.green));
                                break;
                            case "RED":
                                view.setBackgroundColor(getResources().getColor(R.color.red));
                                background.setBackgroundColor(getResources().getColor(R.color.red));
                                break;
                            case "PURPLE":
                                view.setBackgroundColor(getResources().getColor(R.color.purple));
                                background.setBackgroundColor(getResources().getColor(R.color.purple));
                                break;
                            case "Unicorn":
                                view.setBackgroundColor(getResources().getColor(R.color.unicorn));
                                background.setBackgroundColor(getResources().getColor(R.color.unicorn));
                                break;
                        }

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
        return view;
    }

}

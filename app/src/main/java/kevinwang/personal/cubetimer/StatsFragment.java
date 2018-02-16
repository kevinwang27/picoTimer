package kevinwang.personal.cubetimer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kevinwang.personal.cubetimer.db.entity.Solve;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatsFragment extends Fragment {

    private String[] mDescriptions = {"Number of solves:", "Best time:", "Worst Time:", "Session Average:", "Session Mean:", "Current Average of 5:",
            "Best Average of 5:", "Current Average of 12:", "Best Average of 12"};
    private String[] mTimes = new String[mDescriptions.length];
    private List<Long> solveTimes = new ArrayList<>();
    private List<Long> aofs = new ArrayList<>();
    private List<Long> aots = new ArrayList<>();
    protected RecyclerView mRecyclerView;

    public StatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.statRecycler);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Solve> solves = App.get().getDatabase().solveDao().loadAllSolves();
                solveTimes = stringsToLongs(solves);
                initTimes(solves, solveTimes);
            }
        }).start();
        mRecyclerView.setAdapter(new StatsAdapter(mDescriptions, mTimes));
    }

    private List<Long> stringsToLongs(List<Solve> solves) {
        List<Long> result = new ArrayList<>();
        for (Solve solve : solves) {
            long time = stringToLong(solve.getSolveTime());
            result.add(time);
        }
        return result;
    }

    private long stringToLong(String input) {
        int minutes, seconds, milli;
        String[] arr = input.replaceAll("\\s", "").split(":");
        if (arr[arr.length-1].contains("(+2)")) {
            arr[arr.length-1] = arr[arr.length-1].replace("(+2)", "");
        }
        if (arr.length == 2) {
            milli = Integer.parseInt(arr[1]);
            seconds = Integer.parseInt(arr[0]);
            minutes = 0;
        } else {
            milli = Integer.parseInt(arr[2]);
            seconds = Integer.parseInt(arr[1]);
            minutes = Integer.parseInt(arr[0]);
        }
        return minutes*6000 + 100 * seconds + milli;
    }

    private String longToString(long input) {
        int milli = (int) input%100;
        int total_seconds = (int) (input/100);
        int seconds = total_seconds % 60;
        int minutes = (total_seconds / 60) % 60;
        if (minutes > 0) {
            return "" + minutes + ":" + String.format("%02d", seconds) + ":"
                    + String.format("%02d", milli);
        }
        return "" + String.format("%2d", seconds) + ":"
                + String.format("%02d", milli);
    }

    private long calcMean(List<Long> list) {
        long result = 0;
        for (long num : list) {
            result += num;
        }
        return result/list.size();
    }

    private long calcAoN(int n, List<Long> list) {
        Collections.reverse(list);
        long result = 0;
        int count = 0;
        for (long num : list) {
            if (count < n) {
                result += num;
                count++;
            }
        }
        return result/count;
    }

    private void initTimes(List<Solve> solves, List<Long> solveTimes) {
        mTimes[0] = String.valueOf(solves.size());
        mTimes[1] = longToString(Collections.min(solveTimes));
        mTimes[2] = longToString(Collections.max(solveTimes));
        mTimes[3] = "0:00";
        mTimes[4] = longToString(calcMean(solveTimes));
        mTimes[5] = longToString(calcAoN(5, solveTimes));
        mTimes[6] = "0:00";
        mTimes[7] = longToString(calcAoN(12, solveTimes));
        mTimes[8] = "0:00";
    }
}

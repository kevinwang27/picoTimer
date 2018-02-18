package kevinwang.personal.cubetimer;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
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
            "Best Average of 5:", "Current Average of 12:", "Best Average of 12:", "Current Average of 100:"};
    private String[] mTimes = new String[mDescriptions.length];
    private List<Long> solveTimes = new ArrayList<>();
    protected RecyclerView mRecyclerView;
    SharedPreferences sharedPref;

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
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Solve> solves = App.get().getDatabase().solveDao().loadAllSolvesBySession(sharedPref.getString("session", "1"));
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
        input = input.replaceAll("\\s", "");
        String[] arr = input.split(":");
        if (arr[arr.length - 1].contains("(+2)")) {
            arr[arr.length - 1] = arr[arr.length - 1].replace("(+2)", "");
        } else if (arr[0].contains("DNF")) {
            arr = input.replace("DNF(", "").replace(")", "").split(":");
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
        return minutes * 6000 + 100 * seconds + milli;
    }

    private String longToString(long input) {
        int milli = (int) input % 100;
        int total_seconds = (int) (input / 100);
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
        return result / list.size();
    }

    private long calcAoN(int n, List<Long> list) {
        if (list.size() < n) {
            return -1;
        }
        List<Long> copy = new ArrayList<>(list);
        Collections.reverse(copy);
        int count = 0;
        long total = 0;
        List<Long> nums = new ArrayList<>();
        for (long num : copy) {
            if (count < n) {
                total += num;
                nums.add(num);
                count++;
            }
        }
        total -= Collections.min(nums);
        total -= Collections.max(nums);
        return total / (count - 2);
    }

    private long getBestAoN(int n, List<Long> list) {
        if (list.size() < n) {
            return -1;
        } else if (list.size() == n) {
            return calcAoN(n, list);
        } else {
            long currAoF = calcAoN(n, list);
            long restBestAof = getBestAoN(n, removeLastAndReturn(list));
            if (currAoF < restBestAof) {
                return currAoF;
            } else {
                return restBestAof;
            }
        }
    }

    private List<Long> removeLastAndReturn(List<Long> list) {
        List<Long> copy = new ArrayList<>(list);
        copy.remove(copy.size() - 1);
        return copy;
    }

    private void initTimes(List<Solve> solves, List<Long> solveTimes) {
        mTimes[0] = String.valueOf(solves.size());
        if (solveTimes.size() > 0) {
            mTimes[1] = longToString(Collections.min(solveTimes));
            mTimes[2] = longToString(Collections.max(solveTimes));
            if (solveTimes.size() == 1) {
                mTimes[3] = longToString(solveTimes.get(0));
            } else if (solveTimes.size() == 2) {
                mTimes[3] = longToString((solveTimes.get(0) + solveTimes.get(1)) / 2);
            } else {
                mTimes[3] = longToString(calcAoN(solveTimes.size(), solveTimes));
            }
            mTimes[4] = longToString(calcMean(solveTimes));
            if (solveTimes.size() < 5) {
                mTimes[5] = " - ";
                mTimes[6] = " - ";
            } else {
                mTimes[5] = longToString(calcAoN(5, solveTimes));
                mTimes[6] = longToString(getBestAoN(5, solveTimes));
            }
            if (solveTimes.size() < 12) {
                mTimes[7] = " - ";
                mTimes[8] = " - ";
            } else {
                mTimes[7] = longToString(calcAoN(12, solveTimes));
                mTimes[8] = longToString(getBestAoN(12, solveTimes));
            }
            if (solveTimes.size() < 100) {
                mTimes[9] = " - ";
            } else {
                mTimes[9] = longToString(calcAoN(100, solveTimes));
            }
        }
    }
}

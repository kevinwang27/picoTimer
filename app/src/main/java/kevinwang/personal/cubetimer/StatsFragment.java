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
    private List<String> mBestAoFTimes = new ArrayList<>();
    private List<String> mBestAoTTimes = new ArrayList<>();
    private List<String> mCurrAoFTimes = new ArrayList<>();
    private List<String> mCurrAoTTimes = new ArrayList<>();
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

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Solve> solves = App.get().getDatabase().solveDao().loadAllSolvesBySession(sharedPref.getString("session", "1"));
                solveTimes = stringsToLongs(solves);
                initTimes(solves, solveTimes);
            }
        });
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mRecyclerView.setAdapter(new StatsAdapter(mDescriptions, mTimes, mBestAoFTimes, mBestAoTTimes, mCurrAoFTimes, mCurrAoTTimes));
    }

    private List<Long> stringsToLongs(List<Solve> solves) {
        List<Long> result = new ArrayList<>();
        for (Solve solve : solves) {
            long time = stringToLong(solve.getSolveTime());
            result.add(time);
        }
        return result;
    }

    private List<String> longsToStringsWithParens(List<Long> longs) {
        List<String> result = new ArrayList<>();
        boolean max = true, min = true;
        for (Long num : longs) {
            String time = longToString(num);
            if (num == Collections.max(longs) && max) {
                max = false;
                time = "(" + time + ")";
            } else if (num == Collections.min(longs) && min) {
                min = false;
                time = "(" + time + ")";
            }
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
            //arr = input.replace("DNF(", "").replace(")", "").split(":");
            return -1;
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
        if (input == -99) {
            return "DNF";
        }
        int milli = (int) input % 100;
        int total_seconds = (int) (input / 100);
        int seconds = total_seconds % 60;
        int minutes = (total_seconds / 60) % 60;
        if (minutes > 0) {
            return "" + minutes + ":" + String.format("%02d", seconds) + ":"
                    + String.format("%02d", milli);
        }
        if (seconds >= 10) {
            return "" + String.format("%2d", seconds) + ":"
                    + String.format("%02d", milli);
        }
        return "" + String.format("%1d", seconds) + ":"
                + String.format("%02d", milli);
    }

    private long calcMean(List<Long> list) {
        long result = 0;
        for (long num : list) {
            result += num;
        }
        return result / list.size();
    }

    private List<Long> calcAoN(int n, List<Long> list) {
        if (list.size() < n) {
            return null;
        }
        List<Long> copy = new ArrayList<>(list);
        Collections.reverse(copy);
        int count = 0;
        long total = 0;
        int dnf_count = 0;
        List<Long> nums = new ArrayList<>(); //most recent time comes first
        for (long num : copy) {
            if (count < n) {
                if (num >= 0) {
                    total += num;
                    nums.add(num);
                } else {
                    dnf_count++;
                }
                count++;
            }
        }
        if (dnf_count == 0) {
            total -= Collections.min(nums);
            total -= Collections.max(nums);
            nums.add(total / (count - 2));
        } else if (dnf_count == 1) {
            total -= Collections.max(nums);
            nums.add(total / (count - 2));
        } else {
            nums.add(new Long(-99));
        }
        Collections.reverse(nums);
        return nums;
    }

    private List<Long> getBestAoN(int n, List<Long> list) {
        if (list.size() < n) {
            return null;
        } else if (list.size() == n) {
            return calcAoN(n, list);
        } else {
            List<Long> currAoFList = calcAoN(n, list);
            List<Long> restBestAofList = getBestAoN(n, removeLastAndReturn(list));
            long currAoF = currAoFList.get(0);
            long restBestAof = restBestAofList.get(0);
            if ((currAoF < restBestAof && currAoF > 0) || (restBestAof == -99 && currAoF > 0)) {
                return currAoFList;
            } else {
                return restBestAofList;
            }
        }
    }

    private List<Long> removeLastAndReturn(List<Long> list) {
        List<Long> copy = new ArrayList<>(list);
        copy.remove(copy.size() - 1);
        return copy;
    }

    private void initTimes(List<Solve> solves, List<Long> solveTimes) {
        ArrayList<Long> posSolveTimes = new ArrayList<>();
        for (long time : solveTimes) {
            if (time > 0) {
                posSolveTimes.add(time);
            }
        }
        mTimes[0] = String.valueOf(solves.size());
        if (posSolveTimes.size() > 0) {
            mTimes[1] = longToString(Collections.min(posSolveTimes));
            mTimes[2] = longToString(Collections.max(posSolveTimes));
            if (posSolveTimes.size() == 1) {
                mTimes[3] = longToString(posSolveTimes.get(0));
            } else if (posSolveTimes.size() == 2) {
                mTimes[3] = longToString((posSolveTimes.get(0) + posSolveTimes.get(1)) / 2);
            } else {
                mTimes[3] = longToString(calcAoN(posSolveTimes.size(), posSolveTimes).get(0));
            }
            mTimes[4] = longToString(calcMean(posSolveTimes));
            if (solveTimes.size() < 5) {
                mTimes[5] = " - ";
                mTimes[6] = " - ";
            } else {
                List<Long> curr = calcAoN(5, solveTimes);
                mTimes[5] = longToString(curr.remove(0));
                mCurrAoFTimes = longsToStringsWithParens(curr);

                List<Long> best= getBestAoN(5, solveTimes);
                mTimes[6] = longToString(best.remove(0));
                mBestAoFTimes = longsToStringsWithParens(best);
            }
            if (solveTimes.size() < 12) {
                mTimes[7] = " - ";
                mTimes[8] = " - ";
            } else {
                List<Long> curr = calcAoN(12, solveTimes);
                mTimes[7] = longToString(curr.remove(0));
                mCurrAoTTimes = longsToStringsWithParens(curr);

                List<Long> best= getBestAoN(12, solveTimes);
                mTimes[8] = longToString(best.remove(0));
                mBestAoTTimes = longsToStringsWithParens(best);
            }
            if (solveTimes.size() < 100) {
                mTimes[9] = " - ";
            } else {
                mTimes[9] = longToString(calcAoN(100, solveTimes).get(0));
            }
        }
    }
}

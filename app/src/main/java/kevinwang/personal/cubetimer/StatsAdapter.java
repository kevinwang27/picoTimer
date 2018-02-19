package kevinwang.personal.cubetimer;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by kevinwang on 2/16/18.
 */

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.ViewHolder> {
    private String[] mDescriptionSet;
    private String[] mTimeSet;
    private List<String> mBestOfFives;
    private List<String> mBestOfTwelves;
    private List<String> mCurrOfFives;
    private List<String> mCurrOfTwelves;

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView descriptionTextView;
        TextView timeTextView;

        public ViewHolder(View v) {
            super(v);
            cv = (CardView) v.findViewById(R.id.stat_item);
            descriptionTextView = (TextView) v.findViewById(R.id.description);
            timeTextView = (TextView) v.findViewById(R.id.time);
        }
    }

    public StatsAdapter(String[] dataSet, String[] dataSet2, List<String> bestOfFives, List<String> bestOfTwelves, List<String> currFives, List<String> currTwelves) {
        mDescriptionSet = dataSet;
        mTimeSet = dataSet2;
        mBestOfFives = bestOfFives;
        mBestOfTwelves = bestOfTwelves;
        mCurrOfFives = currFives;
        mCurrOfTwelves = currTwelves;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stat_row_item, parent, false);

        return new StatsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.descriptionTextView.setText(mDescriptionSet[position]);
        holder.timeTextView.setText(mTimeSet[position]);
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == 5 && !mCurrOfFives.isEmpty()) {
                    createTimesDialog(mCurrOfFives, holder.cv.getContext());
                } else if (position == 6 && !mBestOfFives.isEmpty()) {
                    createTimesDialog(mBestOfFives, holder.cv.getContext());
                } else if (position == 7 && !mCurrOfTwelves.isEmpty()) {
                    createTimesDialog(mCurrOfTwelves, holder.cv.getContext());
                } else if (position == 8 && !mBestOfTwelves.isEmpty()) {
                    createTimesDialog(mBestOfTwelves, holder.cv.getContext());
                }
            }
        });
    }

    private String concatAllStrings(List<String> times) {
        String result = "";
        for (int i = 0; i < times.size(); i++) {
            result += times.get(i);
            if (i < times.size()-1) {
                result += ", ";
            }
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return mDescriptionSet.length;
    }

    private void createTimesDialog(List<String> times, Context context) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);

        /* make and customize title textview */
        TextView titleView = new TextView((context));
        titleView.setText(concatAllStrings(times));
        titleView.setPadding(40,20,40,20);
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setTextColor(Color.BLACK);
        titleView.setTextSize(20);

        ad.setCustomTitle(titleView);
        ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).show();
    }
}

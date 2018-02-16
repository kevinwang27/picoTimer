package kevinwang.personal.cubetimer;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import kevinwang.personal.cubetimer.db.entity.Solve;

/**
 * Created by kevinwang on 2/16/18.
 */

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.ViewHolder> {
    private String[] mDescriptionSet;
    private String[] mTimeSet;

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

    public StatsAdapter(String[] dataSet, String[] dataSet2) {
        mDescriptionSet = dataSet;
        mTimeSet = dataSet2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stat_row_item, parent, false);

        return new StatsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.descriptionTextView.setText(mDescriptionSet[position]);
        holder.timeTextView.setText(mTimeSet[position]);
        if (position % 2 == 0) {
            holder.cv.setCardBackgroundColor(Color.LTGRAY);
        } else {
            holder.cv.setCardBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return mDescriptionSet.length;
    }
}

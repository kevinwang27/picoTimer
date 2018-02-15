package kevinwang.personal.cubetimer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import kevinwang.personal.cubetimer.db.entity.Solve;

/**
 * Created by kevinwang on 2/13/18.
 */

public class TimesAdapter extends RecyclerView.Adapter<TimesAdapter.ViewHolder> {
    private List<Solve> mDataSet;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageButton arrow;

        public ViewHolder(final View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.textView);
            arrow = (ImageButton) v.findViewById(R.id.delete_button);

            arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            App.get().getDatabase().solveDao().deleteSolveByTime(textView.getText().toString());
                        }
                    }).start();

                    mDataSet.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), mDataSet.size());
                }
            });
        }

    }

    public TimesAdapter(List<Solve> dataSet) {
        mDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(mDataSet.get(position).getSolveTime());
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}

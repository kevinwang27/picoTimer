package kevinwang.personal.cubetimer;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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
        public CardView cv;
        public TextView timeTextView;
        public TextView scrambleTextView;
        public TextView oldTimeTextView;
        public ImageButton arrow;;

        public ViewHolder(final View v) {
            super(v);
            cv = (CardView) v.findViewById(R.id.card_item);
            timeTextView = (TextView) v.findViewById(R.id.time_text);
            scrambleTextView = (TextView) v.findViewById(R.id.scramble_text);
            oldTimeTextView = (TextView) v.findViewById(R.id.old_time);
            arrow = (ImageButton) v.findViewById(R.id.arrow);

            arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder ad = new AlertDialog.Builder(v.getContext());
                    LayoutInflater layoutInflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View dialog = layoutInflater.inflate(R.layout.dialog_solve, null);

                    TextView title = (TextView) dialog.findViewById(R.id.dialog_title);
                    TextView message = (TextView) dialog.findViewById(R.id.dialog_message);
                    Button cleanButton = (Button) dialog.findViewById(R.id.clean_button);
                    Button plusTwoButton = (Button) dialog.findViewById(R.id.plus_two_button);
                    Button dnfButton = (Button) dialog.findViewById(R.id.dnf_button);
                    Button deleteButton = (Button) dialog.findViewById(R.id.delete_button);

                    title.setText(timeTextView.getText().toString().replaceAll("\\s", ""));
                    message.setText(scrambleTextView.getText().toString());

                    ad.setView(dialog);
                    final AlertDialog alertDialog = ad.show();

                    cleanButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setSolveTime(oldTimeTextView.getText().toString());
                            alertDialog.dismiss();
                        }
                    });
                    plusTwoButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addTwoSeconds();
                            alertDialog.dismiss();
                        }
                    });
                    dnfButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setSolveTime(" DNF(" + oldTimeTextView.getText().toString()
                                    .replaceAll("\\s", "") + ")");
                            alertDialog.dismiss();
                        }
                    });
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            removeSolve();
                            alertDialog.dismiss();
                        }
                    });
                }
            });
        }

        private void setSolveTime(final String time) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    App.get().getDatabase().solveDao().setSolveTimeByScramble(scrambleTextView.getText().toString(), time);
                }
            }).start();
            timeTextView.setText(time);
        }

        private String addTwoSecondsToString(String time_input) {
            int minutes, seconds, milli;
            String[] arr = time_input.replaceAll("\\s", "").split(":");
            if (arr.length == 2) {
                milli = Integer.parseInt(arr[1]);
                seconds = Integer.parseInt(arr[0]);
                minutes = 0;
            } else {
                milli = Integer.parseInt(arr[2]);
                seconds = Integer.parseInt(arr[1]);
                minutes = Integer.parseInt(arr[0]);
            }
            seconds += 2;
            if (seconds >= 60) {
                seconds %= 60;
                minutes += 1;
            }
            if (minutes > 0) {
                return "" + minutes + ":" + String.format("%02d", seconds) + ":"
                        + String.format("%02d", milli);
            }
            return "" + String.format("%2d", seconds) + ":"
                    + String.format("%02d", milli);
        }

        private void addTwoSeconds() {
            final String curr_solve_time = oldTimeTextView.getText().toString();
            setSolveTime(addTwoSecondsToString(curr_solve_time) + "(+2)");
        }

        private void removeSolve() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    App.get().getDatabase().solveDao().deleteSolveByTime(timeTextView.getText().toString());
                }
            }).start();

            mDataSet.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
            notifyItemRangeChanged(getAdapterPosition(), mDataSet.size());
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
        holder.timeTextView.setText(mDataSet.get(position).getSolveTime());
        holder.scrambleTextView.setText(mDataSet.get(position).getScramble());
        holder.oldTimeTextView.setText(mDataSet.get(position).getOldTime());
        if (position % 2 == 0) {
            holder.cv.setCardBackgroundColor(Color.LTGRAY);
        } else {
            holder.cv.setCardBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}

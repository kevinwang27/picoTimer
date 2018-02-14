package kevinwang.personal.cubetimer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import kevinwang.personal.cubetimer.db.entity.Solve;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimesFragment extends Fragment {
    protected RecyclerView mRecyclerView;

    public TimesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_times, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.timeRecycler);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Solve> solves = App.get().getDatabase().solveDao().loadAllSolves();
                initDataSet(solves);
            }
        }).start();
    }

    private void initDataSet(final List<Solve> solves) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.setAdapter(new TimesAdapter(solves));
            }
        });
    }
}

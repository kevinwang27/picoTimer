package kevinwang.personal.cubetimer;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView mBottomNavigationView;
    Fragment firstFragment;
    Fragment timesFrag;
    Fragment settingsFrag;
    Fragment statsFrag;
    Toolbar toolbar;
    boolean custom_enabled;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View view = layoutInflater.inflate(R.layout.action_bar_with_options, null);
        final TextView mTitleTextView = (TextView) view.findViewById(R.id.title_text);
        final ImageButton imageButton = (ImageButton) view.findViewById(R.id.imageButton);

        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);

        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_settings:
                                settingsFrag = new TimesFragment();
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                if (!firstFragment.isHidden()) {
                                    transaction.hide(firstFragment);
                                }
                                transaction.add(R.id.entire_view, settingsFrag);
                                transaction.addToBackStack(null);
                                transaction.commit();
                                getSupportActionBar().show();
                                getSupportActionBar().setTitle("Solves");
                                break;
                            case R.id.action_solves:
                                timesFrag = new TimesFragment();
                                final FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                                if (!firstFragment.isHidden()) {
                                    transaction2.hide(firstFragment);
                                }
                                transaction2.add(R.id.entire_view, timesFrag);
                                transaction2.addToBackStack(null);
                                transaction2.commit();

                                mTitleTextView.setText("Solves");
                                imageButton.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        createAlertAndDelete(timesFrag.getActivity());
                                    }
                                });

                                getSupportActionBar().setCustomView(view);
                                getSupportActionBar().setDisplayShowCustomEnabled(true);
                                custom_enabled = true;
                                getSupportActionBar().show();
                                break;

                            case R.id.action_stats:
                                statsFrag = new StatsFragment();
                                FragmentTransaction transaction4 = getSupportFragmentManager().beginTransaction();
                                if (!firstFragment.isHidden()) {
                                    transaction4.hide(firstFragment);
                                }
                                transaction4.add(R.id.entire_view, statsFrag);
                                transaction4.addToBackStack(null);
                                transaction4.commit();

                                getSupportActionBar().setDisplayShowCustomEnabled(false);
                                custom_enabled = false;
                                getSupportActionBar().setTitle("Stats");
                                getSupportActionBar().show();
                                break;
                            case R.id.action_timer:
                                FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                                transaction3.replace(R.id.entire_view, firstFragment);
                                transaction3.show(firstFragment);
                                transaction3.commit();
                                getSupportActionBar().hide();
                                break;
                        }
                        return false;
                    }
                });

        if (findViewById(R.id.entire_view) != null) {

            if (savedInstanceState != null) {
                firstFragment = getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
                boolean custom_visible = savedInstanceState.getBoolean("custom_visible");
                if (savedInstanceState.getBoolean("action_bar_visible") && !custom_visible) {
                    getSupportActionBar().show();
                    getSupportActionBar().setTitle(savedInstanceState.getString("action_bar_title"));
                } else if (savedInstanceState.getBoolean("action_bar_visible") && custom_visible){
                    getSupportActionBar().show();
                    getSupportActionBar().setCustomView(view);
                    custom_enabled = true;
                    mTitleTextView.setText("Solves");
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            createAlertAndDelete(firstFragment.getActivity());
                        }
                    });
                    getSupportActionBar().setDisplayShowCustomEnabled(true);
                } else {
                    getSupportActionBar().hide();
                }
                return;
            }

            firstFragment = new TimerFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.entire_view, firstFragment).commit();

            getSupportActionBar().hide();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "myFragmentName", firstFragment);
        outState.putBoolean("action_bar_visible", getSupportActionBar().isShowing());
        outState.putBoolean("custom_visible", custom_enabled);
        outState.putCharSequence("action_bar_title", getSupportActionBar().getTitle());
    }

    private void createAlertAndDelete(Context context) {
        AlertDialog.Builder ad = new AlertDialog.Builder(context);
        ad.setTitle("Delete all solves?");
        ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        App.get().getDatabase().solveDao().clearSolves();
                    }
                }).start();
                timesFrag = new TimesFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.entire_view, timesFrag).commit();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).show();
    }
}

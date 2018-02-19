package kevinwang.personal.cubetimer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    View background;
    BottomNavigationView mBottomNavigationView;
    Fragment timerFrag;
    Fragment timesFrag;
    Fragment settingsFrag;
    Fragment statsFrag;
    Toolbar toolbar;
    boolean custom_enabled;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        background = findViewById(R.id.container);
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        /* setup toolbar */
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
                                settingsFrag = new SettingsFragment();
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                if (timerFrag.isVisible()) {
                                    transaction.hide(timerFrag);
                                }
                                transaction.add(R.id.entire_view, settingsFrag).commit();

                                getSupportActionBar().setDisplayShowCustomEnabled(false);
                                custom_enabled = false;
                                getSupportActionBar().setTitle("Settings");
                                setToolbarSettings(toolbar);
                                getSupportActionBar().show();
                                break;
                            case R.id.action_solves:
                                timesFrag = new TimesFragment();
                                final FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                                if (!timerFrag.isHidden()) {
                                    transaction2.hide(timerFrag);
                                }
                                transaction2.add(R.id.entire_view, timesFrag).commit();
                                mTitleTextView.setText("Solves: Session " + sharedPref.getString("session", "1"));
                                imageButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        createAlertAndDelete(timesFrag.getActivity());
                                    }
                                });

                                setCustomToolbarSettings(view, mTitleTextView, imageButton);
                                setToolbarSettings(toolbar);

                                getSupportActionBar().setCustomView(view);
                                getSupportActionBar().setDisplayShowCustomEnabled(true);
                                custom_enabled = true;
                                getSupportActionBar().show();
                                break;
                            case R.id.action_stats:
                                statsFrag = new StatsFragment();
                                FragmentTransaction transaction4 = getSupportFragmentManager().beginTransaction();
                                if (!timerFrag.isHidden()) {
                                    transaction4.hide(timerFrag);
                                }
                                transaction4.add(R.id.entire_view, statsFrag).commit();

                                getSupportActionBar().setDisplayShowCustomEnabled(false);
                                custom_enabled = false;
                                getSupportActionBar().setTitle("Stats: Session " + sharedPref.getString("session", "1"));
                                setToolbarSettings(toolbar);
                                getSupportActionBar().show();
                                break;
                            case R.id.action_timer:
                                if (timerFrag.isVisible()) {
                                    break;
                                }
                                timerFrag = new TimerFragment();
                                getSupportFragmentManager().beginTransaction().replace(R.id.entire_view, timerFrag).show(timerFrag).commit();

                                getSupportActionBar().hide();
                                break;
                        }
                        return false;
                    }
                });

        if (findViewById(R.id.entire_view) != null) {

            if (savedInstanceState != null) {
                timerFrag = getSupportFragmentManager().getFragment(savedInstanceState, "timerFrag");
                boolean custom_visible = savedInstanceState.getBoolean("custom_visible");
                if (savedInstanceState.getBoolean("action_bar_visible")) {
                    setToolbarSettings(toolbar);
                    getSupportActionBar().show();
                    if (!custom_visible) {
                        getSupportActionBar().setTitle(savedInstanceState.getString("action_bar_title"));
                    } else {
                        setCustomToolbarSettings(view, mTitleTextView, imageButton);
                        getSupportActionBar().setCustomView(view);
                        custom_enabled = true;
                        mTitleTextView.setText("Solves: Session " + sharedPref.getString("session", "1"));
                        imageButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                createAlertAndDelete(timerFrag.getActivity());
                            }
                        });
                        getSupportActionBar().setDisplayShowCustomEnabled(true);
                    }
                } else {
                    getSupportActionBar().hide();
                }
                return;
            }

            timerFrag = new TimerFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.entire_view, timerFrag).commit();

            getSupportActionBar().hide();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("first_launch", false).apply();
    }

    @Override
    public void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("first_launch", true).apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "timerFrag", timerFrag);
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
                        App.get().getDatabase().solveDao().clearSolvesBySession(sharedPref.getString("session", "1"));
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

    private void setToolbarSettings(Toolbar toolbar) {
        switch (sharedPref.getString("theme", "Black")) {
            case "Black":
                toolbar.setBackgroundColor(getResources().getColor(R.color.black));
                break;
            case "White":
                toolbar.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case "Blue":
                toolbar.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
            case "Green":
                toolbar.setBackgroundColor(getResources().getColor(R.color.green));
                break;
            case "Red":
                toolbar.setBackgroundColor(getResources().getColor(R.color.red));
                break;
            case "Purple":
                toolbar.setBackgroundColor(getResources().getColor(R.color.purple));
                break;
            case "Unicorn":
                toolbar.setBackgroundColor(getResources().getColor(R.color.unicorn));
                break;
        }
        if (sharedPref.getString("theme", "Black").equals("White")) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }
    }

    private void setCustomToolbarSettings(View toolbar, TextView timerText, ImageButton imgbutton) {
        switch (sharedPref.getString("theme", "Black")) {
            case "Black":
                toolbar.setBackgroundColor(getResources().getColor(R.color.black));
                break;
            case "White":
                toolbar.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case "Blue":
                toolbar.setBackgroundColor(getResources().getColor(R.color.blue));
                break;
            case "Green":
                toolbar.setBackgroundColor(getResources().getColor(R.color.green));
                break;
            case "Red":
                toolbar.setBackgroundColor(getResources().getColor(R.color.red));
                break;
            case "Purple":
                toolbar.setBackgroundColor(getResources().getColor(R.color.purple));
                break;
            case "Unicorn":
                toolbar.setBackgroundColor(getResources().getColor(R.color.unicorn));
                break;
        }
        if (sharedPref.getString("theme", "Black").equals("White")) {
            timerText.setTextColor(getResources().getColor(R.color.black));
            imgbutton.setImageResource(R.drawable.ic_delete_button);
        } else {
            timerText.setTextColor(getResources().getColor(R.color.white));
            imgbutton.setImageResource(R.drawable.ic_delete_button_white);
        }
    }
}

package kevinwang.personal.cubetimer;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

    BottomNavigationView mBottomNavigationView;
    Fragment firstFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_settings:
                                //hide the timer
                                break;
                            case R.id.action_stats:
                                TimesFragment timesFrag = new TimesFragment();
                                FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                                transaction2.replace(R.id.entire_view, timesFrag);
                                transaction2.addToBackStack(null);
                                transaction2.commit();
                                break;
                            case R.id.action_timer:
                                //show the timer
                                //replace this code
                                TimerFragment tFrag = new TimerFragment();
                                FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                                transaction3.replace(R.id.entire_view, tFrag);
                                transaction3.addToBackStack(null);
                                transaction3.commit();
                                break;
                        }
                        return false;
                    }
                });

        if (findViewById(R.id.entire_view) != null) {

            if (savedInstanceState != null) {
                return;
            }

            firstFragment = new TimerFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.entire_view, firstFragment).commit();
        }
    }
}

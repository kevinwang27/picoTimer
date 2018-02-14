package kevinwang.personal.cubetimer;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

    BottomNavigationView mBottomNavigationView;
    Fragment firstFragment;
    Fragment timesFrag;
    Fragment settingsFrag;

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
                                settingsFrag = new TimesFragment();
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                if (!firstFragment.isHidden()) {
                                    transaction.hide(firstFragment);
                                }
                                transaction.add(R.id.entire_view, settingsFrag);
                                transaction.addToBackStack(null);
                                transaction.commit();
                                break;
                            case R.id.action_stats:
                                timesFrag = new TimesFragment();
                                FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                                if (!firstFragment.isHidden()) {
                                    transaction2.hide(firstFragment);
                                }
                                transaction2.add(R.id.entire_view, timesFrag);
                                transaction2.addToBackStack(null);
                                transaction2.commit();
                                break;
                            case R.id.action_timer:
                                FragmentTransaction transaction3 = getSupportFragmentManager().beginTransaction();
                                transaction3.replace(R.id.entire_view, firstFragment);
                                transaction3.show(firstFragment);
                                transaction3.commit();
                                break;
                        }
                        return false;
                    }
                });

        if (findViewById(R.id.entire_view) != null) {

            if (savedInstanceState != null) {
                firstFragment = getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
                return;
            }

            firstFragment = new TimerFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.entire_view, firstFragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "myFragmentName", firstFragment);
    }
}

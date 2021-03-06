package kevinwang.personal.cubetimer;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;

/**
 * A simple {@link PreferenceFragment} subclass.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    View background;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.white));

        return view;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        Preference session = findPreference("session");
        session.setSummary(sharedPreferences.getString("session", "1"));

        Preference theme = findPreference("theme");
        theme.setSummary(sharedPreferences.getString("theme", "Black"));

        Preference clearAll = findPreference("clear_all");
        clearAll.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                ad.setTitle(R.string.delete_all_sessions_dialogText);
                ad.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                App.get().getDatabase().solveDao().clearSolves();
                            }
                        }).start();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("first_launch", true).apply();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        background = getActivity().findViewById(R.id.container);
        background.setBackgroundColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("session")) {
            Preference session = findPreference(s);
            session.setSummary(sharedPreferences.getString(s, "1"));
        } else if (s.equals("theme")) {
            Preference theme = findPreference(s);
            theme.setSummary(sharedPreferences.getString(s, "Black"));
        }
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        switch (sharedPreferences.getString("theme", "Black")) {
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
            case "Pink":
                toolbar.setBackgroundColor(getResources().getColor(R.color.unicorn));
                break;
        }
        
        if (sharedPreferences.getString("theme", "Black").equals("White")) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}

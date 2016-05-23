package eit42.der_onlinestundenplan;

/**
 * Created by L.Schnitzmeier on 19.05.2016.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.renderscript.Sampler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import eit42.der_onlinestundenplan.data.DBHelper;
import eit42.der_onlinestundenplan.data.SchoolContract;

public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    String schoollistKey;
    String classlistKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        schoollistKey = getString(R.string.preference_schoollist_key);
        classlistKey = getString(R.string.preference_classlist_key);

        //noinspection deprecation
        addPreferencesFromResource(R.xml.preferences);

        //noinspection deprecation
        ListPreference schoollistPref = (ListPreference) findPreference(schoollistKey);
        schoollistPref.setOnPreferenceChangeListener(this);
        final String schoolDef = getString(R.string.preference_schoollist_default);

        //noinspection deprecation
        ListPreference classlistPref = (ListPreference) findPreference(classlistKey);
        classlistPref.setOnPreferenceChangeListener(this);
        classlistPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });
        String classDef = getString(R.string.preference_classlist_default);


        String sPrefsBaseKey = getString(R.string.shared_preference_base_key);

        sharedPrefs = this.getSharedPreferences(sPrefsBaseKey, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPrefs.edit();

        String savedSchoolList = sharedPrefs.getString(schoollistPref.getKey(), "");
        onPreferenceChange(schoollistPref, savedSchoolList);

        String savedClassList = sharedPrefs.getString(classlistPref.getKey(), "");
        onPreferenceChange(classlistPref,savedClassList);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {

        preference.setSummary(value.toString());
        editor.putString(preference.getKey(),value.toString());
        editor.apply();

        return true;
    }

}

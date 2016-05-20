package eit42.der_onlinestundenplan;

/**
 * Created by L.Schnitzmeier on 19.05.2016.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.renderscript.Sampler;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection deprecation
        addPreferencesFromResource(R.xml.preferences);

        //noinspection deprecation
        Preference schoollistPref = findPreference(getString(R.string.preference_schoollist_key));
        schoollistPref.setOnPreferenceChangeListener(this);
        String schoolDef = getString(R.string.preference_schoollist_default);

        //noinspection deprecation
        Preference classlistPref = findPreference(getString(R.string.preference_classlist_key));
        classlistPref.setOnPreferenceChangeListener(this);
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

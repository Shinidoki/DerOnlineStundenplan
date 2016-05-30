package eit42.der_onlinestundenplan;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

import eit42.der_onlinestundenplan.data.StundenPlanApi;

public class TimeTableActivity extends AppCompatActivity {


    private TimeTableFragmentAdapter mTimeTableFragmentAdapter;
    private ViewPager mViewPager;
    private ImageButton lastWeekButton, nextWeekButton;
    private int currentWeek;

    private Toolbar topToolbar;
    private Toolbar bottomToolbar;
    private TextView toolbarSubtitle;
    private View progressOverlay;

    //Shared Preferences
    private SharedPreferences sPrefs;
    private String sPrefsBaseKey ;
    private String sPrefsSubtitleKey;

    String prefSchoollistKey;
    String prefSchoollistDefault;
    String prefClasslistKey;
    String prefClasslistDefault;

    String currentSchool;
    String currentClass;

    static int weekCounter = 0;
    static int weekCounterBackup = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        loadPrefSettings();
        loadCurrentPrefs();

        //Toolbar initialization
        topToolbar = (Toolbar) findViewById(R.id.app_bar);
        bottomToolbar = (Toolbar) findViewById(R.id.bottom_toolbar);
        updateSubtitleText();
        setSupportActionBar(topToolbar);
        topToolbar.inflateMenu(R.menu.menu_time_table);
        mTimeTableFragmentAdapter = new TimeTableFragmentAdapter(getSupportFragmentManager());

        progressOverlay = findViewById(R.id.progress_overlay);

        lastWeekButton = (ImageButton) bottomToolbar.findViewById(R.id.lastWeekButton);
        nextWeekButton = (ImageButton) bottomToolbar.findViewById(R.id.nextWeekButton);

        changeWeekText();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        currentSchool = sharedPref.getString(getString(R.string.preference_schoollist_key),"RvWBK");
        currentClass = sharedPref.getString(getString(R.string.preference_classlist_key),"EIT42");
        new SettingsAsyncTask().execute(currentSchool);
        Calendar cal = new GregorianCalendar();
        currentWeek = cal.get(GregorianCalendar.WEEK_OF_YEAR);

        new TimeAsyncTask().execute(currentSchool,currentClass);

        nextWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekCounter++;
                new TimeAsyncTask().execute(currentSchool, currentClass);
                changeWeekText();
            }
        });

        lastWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weekCounter--;
                new TimeAsyncTask().execute(currentSchool,currentClass);
                changeWeekText();
            }
        });
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mTimeTableFragmentAdapter);


    }

    private void changeWeekText()
    {
        final TextView weekText = (TextView) bottomToolbar.findViewById(R.id.weekTextView);
        weekText.setText("Woche " + weekCounter);
    }


    public void updateSubtitleText()
    {
        if(toolbarSubtitle == null) {
            toolbarSubtitle = (TextView) topToolbar.findViewById(R.id.toolbar_subtitle);
        }
        if(currentClass != "" && currentSchool != "")
        {
            toolbarSubtitle.setText(currentClass + " | " + currentSchool);
        }else toolbarSubtitle.setText(getString(R.string.no_school_error_message));

    }

    public void loadPrefSettings()
    {
        sPrefsBaseKey = getString(R.string.shared_preference_base_key);
        sPrefsSubtitleKey = getString(R.string.shared_preference_toolbar_key)+".subtitle";

        sPrefs = this.getSharedPreferences(sPrefsBaseKey,Context.MODE_PRIVATE);

        //Get school key & default
        prefSchoollistKey = getString(R.string.preference_schoollist_key);
        prefSchoollistDefault = getString(R.string.preference_schoollist_default);
        //Get school key & default
        prefClasslistKey = getString(R.string.preference_classlist_key);
        prefClasslistDefault = getString(R.string.preference_classlist_default);

    }

    public void loadCurrentPrefs()
    {
        //Set current values
        currentSchool = sPrefs.getString(prefSchoollistKey,prefSchoollistDefault);
        currentClass = sPrefs.getString(prefClasslistKey,prefClasslistDefault);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_table, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }else if(id == R.id.action_refresh)
        {
            loadCurrentPrefs();
            updateSubtitleText();

            new TimeAsyncTask().execute(currentSchool,currentClass);
            Toast.makeText(this,"Stundenplan aktualisiert",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void refresh()
    {
        new TimeAsyncTask().execute(currentSchool, currentClass);
    }

    private class TimeAsyncTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
                }
            });

            StundenPlanApi api = new StundenPlanApi(getApplicationContext());
            return api.getClassInfo(params[0],params[1],weekCounter+currentWeek);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(JSONObject result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    animateView(progressOverlay, View.GONE, 0, 200);
                }
            });
            if(result != null){
                mTimeTableFragmentAdapter.setFragments(result);
                weekCounterBackup = weekCounter;
            } else {
                weekCounter = weekCounterBackup;
                AlertDialog alertDialog = new AlertDialog.Builder(TimeTableActivity.this).create();
                alertDialog.setTitle("Keine Daten!");
                alertDialog.setMessage("Es konnten keine Daten zu dieser Woche abgerufen werden!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                changeWeekText();
            }
        }
    }

    private class SettingsAsyncTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            StundenPlanApi api = new StundenPlanApi(getApplicationContext());
            api.getSchools();
            if(!params[0].isEmpty()){
                api.getClasses(params[0]);
            } else {
                api.getClasses("RvWBK");
            }
            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(JSONObject result) {

        }
    }

    /**
     * @param view         View to animate
     * @param toVisibility Visibility at the end of animation
     * @param toAlpha      Alpha at the end of animation
     * @param duration     Animation duration in ms
     */
    public static void animateView(final View view, final int toVisibility, float toAlpha, int duration) {
        boolean show = toVisibility == View.VISIBLE;
        if (show) {
            view.setAlpha(0);
        }
        view.setVisibility(View.VISIBLE);
        view.animate()
                .setDuration(duration)
                .alpha(show ? toAlpha : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(toVisibility);
                    }
                });
    }
}

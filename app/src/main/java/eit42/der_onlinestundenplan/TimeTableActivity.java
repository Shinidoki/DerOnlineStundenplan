package eit42.der_onlinestundenplan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class TimeTableActivity extends AppCompatActivity {


    private TimeTableFragmentAdapter mTimeTableFragmentAdapter;
    private ViewPager mViewPager;
    private ImageButton lastWeekButton;
    private ImageButton nextWeekButton;
    private TextView weekTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        initToolbars();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mTimeTableFragmentAdapter = new TimeTableFragmentAdapter(getSupportFragmentManager());
        mTimeTableFragmentAdapter.setFragments(this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mTimeTableFragmentAdapter);


    }

    public void initToolbars()
    {
        Toolbar topToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(topToolbar);
        topToolbar.inflateMenu(R.menu.menu_time_table);

        Toolbar bottomToolbar = (Toolbar) findViewById(R.id.bottom_toolbar);
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
            SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(this);

            String prefSchoollistKey = getString(R.string.preference_schoollist_key);
            String prefSchoollistDefault = getString(R.string.preference_schoollist_default);
            String schoollist = sPrefs.getString(prefSchoollistKey,prefSchoollistDefault);

            String prefClasslistKey = getString(R.string.preference_classlist_key);
            String prefClasslistDefault = getString(R.string.preference_classlist_default);
            String classlist = sPrefs.getString(prefClasslistKey,prefClasslistDefault);

            Toast.makeText(this,"Stundenplan wird aktualisiert",Toast.LENGTH_SHORT).show();
            Toast.makeText(this,classlist + ";" + schoollist,Toast.LENGTH_SHORT).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

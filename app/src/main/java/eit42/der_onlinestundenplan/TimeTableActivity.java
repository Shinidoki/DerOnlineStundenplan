package eit42.der_onlinestundenplan;

import android.content.Intent;
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.menu_time_table);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mTimeTableFragmentAdapter = new TimeTableFragmentAdapter(getSupportFragmentManager());
        mTimeTableFragmentAdapter.setFragments(this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mTimeTableFragmentAdapter);

        weekTextView = (TextView) findViewById(R.id.weekText);
        nextWeekButton = (ImageButton) findViewById(R.id.nextWeekButton);
        lastWeekButton = (ImageButton) findViewById(R.id.lastWeekButton);


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
            Intent intent = new Intent(TimeTableActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

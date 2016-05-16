package eit42.der_onlinestundenplan;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class TimeTableActivity extends AppCompatActivity {


    private TimeTableFragmentAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private ImageButton nextWeekBtn, lastWeekBtn;
    private int weekCounter = 0;
    private int currentWeek;
    private Fragment timeTableFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        final String school = "RvWBK";
        final String sClass = "EIT42";
        Calendar cal = new GregorianCalendar();
        currentWeek = cal.get(GregorianCalendar.WEEK_OF_YEAR);
        final StundenPlanApi api = new StundenPlanApi(this);

        new TimeAsyncTask().execute(school,sClass);

        nextWeekBtn = (ImageButton) findViewById(R.id.nextWeekButton);
        nextWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(weekCounter < 3)
                    weekCounter++;
                new TimeAsyncTask().execute(school,sClass);
            }
        });

        lastWeekBtn = (ImageButton) findViewById(R.id.lastWeekButton);
        lastWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(weekCounter > -3)
                    weekCounter--;
                JSONObject timeTable = api.getClassInfo(school, sClass, currentWeek+weekCounter);
                Bundle bundle = new Bundle();
                bundle.putString("timeTable", timeTable.toString());
                TimeTableFragment fragInfo = new TimeTableFragment();
                fragInfo.setArguments(bundle);
            }
        });
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class TimeAsyncTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            StundenPlanApi api = new StundenPlanApi(getApplicationContext());
            return api.getClassInfo(params[0],params[1],weekCounter+currentWeek);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(JSONObject result) {
            mSectionsPagerAdapter = new TimeTableFragmentAdapter(getSupportFragmentManager());
            mSectionsPagerAdapter.setFragments(result);
            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
    }
}

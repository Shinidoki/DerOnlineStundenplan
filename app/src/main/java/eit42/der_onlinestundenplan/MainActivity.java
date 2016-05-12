package eit42.der_onlinestundenplan;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {

    EditText etResponse;
    TextView tvIsConnected;
    Spinner schools;
    Spinner classes;
    Button testButton;
    private Toolbar toolbar;
    TimeTable timeTableActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Toolbar stuff
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        toolbar.setLogo(R.drawable.logo);
        toolbar.inflateMenu(R.menu.menu_main);

        //Switch to TimeTable Activity Button
        testButton = (Button) findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TimeTable.class);
                startActivity(intent);
            }
        });

        // get reference to the views
        etResponse = (EditText) findViewById(R.id.etResponse);
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        schools = (Spinner) findViewById(R.id.schools);
        classes = (Spinner) findViewById(R.id.classes);

        schools.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String school = String.valueOf(schools.getSelectedItem());
                new ClassesAsyncTask().execute(school);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}

        });


        // check if you are connected or not
        if(isConnected()){
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are conncted");
        }
        else{
            tvIsConnected.setText("You are NOT conncted");
        }

        // call AsynTask to perform network operation on separate thread
        new SchoolsAsyncTask().execute();
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private class SchoolsAsyncTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... param) {
            StundenPlanApi api = new StundenPlanApi(getApplicationContext());
            return api.getSchoolsArray();
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String[] result) {
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            try{
//                etResponse.setText(result.toString(1));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                        android.R.layout.simple_spinner_item, result);
                schools.setAdapter(adapter);


            }catch(Exception e) {
                etResponse.setText("Fehler beim holen der Daten");
            }

        }
    }


    private class ClassesAsyncTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... school) {
            StundenPlanApi api = new StundenPlanApi(getApplicationContext());
            return api.getClassesArray(school[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String[] result) {
            Toast.makeText(getBaseContext(), "Classes Received!", Toast.LENGTH_LONG).show();
            try{
//                etResponse.setText(result.toString(1));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                        android.R.layout.simple_spinner_item, result);
                classes.setAdapter(adapter);


            }catch(Exception e) {
                etResponse.setText("Fehler beim holen der Daten");
            }

        }
    }
}
package eit42.der_onlinestundenplan;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import eit42.der_onlinestundenplan.data.DBHelper;
import eit42.der_onlinestundenplan.data.SchoolContract;

public class StundenPlanApi {
    private static final String url = "http://beta.der-onlinestundenplan.de/api/v1/school";

    private JSONArray schools = null;
    private JSONObject classes = null;
    private Context context;

    public StundenPlanApi(Context context)
    {
        this.context = context;
    }

    /**
     * Gets all schools from the api if not already fetched
     *
     * @return JSONArray of all Schools
     */
    public JSONArray getSchools()
    {
        if(schools != null){
            return schools;
        }

        String apiResult = apiCall("");

        JSONArray result = null;
        try {
            JSONObject json = new JSONObject(apiResult);
            result = json.getJSONArray("schools");
        } catch(Exception e){
            System.out.println("Json error: "+e.getMessage());
            return null;
        }
        schools = result;
        saveSchools(result);
        return result;
    }

    private void saveSchools(JSONArray schools)
    {
        int len = schools.length();
        DBHelper dbHelper = new DBHelper(context,SchoolContract.SQL_CREATE_ENTRIES,SchoolContract.SQL_DELETE_ENTRIES);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (int i = 0; i < len; i++) {
            try {
                JSONObject school = schools.getJSONObject(i);
                ContentValues values = new ContentValues();
                values.put(SchoolContract.SchoolEntry.COLUMN_NAME_S_NAME, school.getString("name"));
                values.put(SchoolContract.SchoolEntry.COLUMN_NAME_S_CITY, school.getString("city"));
                values.put(SchoolContract.SchoolEntry.COLUMN_NAME_S_WEBSITE, school.getString("website"));
                db.insert(
                        SchoolContract.SchoolEntry.TABLE_NAME,
                        null,
                        values
                );
            } catch(Exception e){
                System.out.println("Fehler beim speichern der Schule in der Datenbank: "+e.getMessage());
            }
        }
    }

    /**
     * Get all Schools as an String array
     * @return String array of all schools
     */
    public String[] getSchoolsArray()
    {
        DBHelper dbHelper = new DBHelper(context,SchoolContract.SQL_CREATE_ENTRIES,SchoolContract.SQL_DELETE_ENTRIES);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                SchoolContract.SchoolEntry.COLUMN_NAME_S_NAME
        };

        String sortOrder = SchoolContract.SchoolEntry.COLUMN_NAME_S_NAME + " ASC";

        Cursor c = db.query(
                SchoolContract.SchoolEntry.TABLE_NAME,
                projection,
                null,null,null,null,
                sortOrder
        );

        int count = c.getCount();

        if(count == 0){
            c.close();
            JSONArray schoolArray = getSchools();
            int len = schoolArray.length();
            String[] result = new String[len];

            for (int i = 0; i < len; i++) {
                try {
                    result[i] = schoolArray.getJSONObject(i).getString("name");
                } catch(Exception e){
                    System.out.println("Fehler beim konvertieren der Schulen ins Array format: "+e.getMessage());
                }
            }
            return result;
        } else {
            c.moveToFirst();
            String[] result = new String[count];
            for (int i = 0; i < count; i++) {
                result[i] = c.getString(c.getColumnIndex(SchoolContract.SchoolEntry.COLUMN_NAME_S_NAME));
            }
            c.close();
            return result;
        }
    }

    /**
     * Get more information about a school
     *
     * @param school The school name
     * @return Information about the school (name, city, website)
     */
    public JSONObject getSchoolInfo(String school)
    {
        String apiResult = apiCall("/" + school);

        JSONObject result = null;
        try {
            result = new JSONObject(apiResult);
        } catch(Exception e){
            System.out.println("Json error: "+e.getMessage());
            return null;
        }
        return result;
    }

    /**
     * Get all classes of a school from the api. Takes data from object variable if already found
     *
     * @param school The school name
     * @return All available classes of a school
     */
    public JSONArray getClasses(String school)
    {
        if(classes == null){
            classes = new JSONObject();
        }
        if(classes.has(school)){
            try{
                return classes.getJSONArray(school);
            } catch(Exception e){
                System.out.println("Fehler beim holen der Klassen: "+e.getMessage());
            }
        }

        String apiResult = apiCall("/" + school+"/class");

        JSONObject result = null;
        JSONArray arrayResult = null;
        try {
            result = new JSONObject(apiResult);

            arrayResult = result.getJSONArray("classes");

            classes.put(school, arrayResult);

        } catch(Exception e){
            System.out.println("Json error: "+e.getMessage());
            return null;
        }

        return arrayResult;
    }

    /**
     * Get all classes as a String array
     * @return String array of all classes
     */
    public String[] getClassesArray(String school)
    {
        JSONArray classesArray = getClasses(school);
        int len = classesArray.length();
        String[] result = new String[len];

        for (int i = 0; i < len; i++) {
            try {
                result[i] = classesArray.getString(i);
            } catch(Exception e){
                System.out.println("Fehler beim konvertieren der Klassen ins Array format: "+e.getMessage());
            }
        }

        return result;
    }


    /**
     * Get more information about a class
     *
     * @param school The name of the school
     * @param sClass The class name
     * @return Information about the class (currentWeek, Timetable, lastUpdated)
     */
    public JSONObject getClassInfo(String school, String sClass)
    {
        return classApiCall("/" + school + "/class/" + sClass);
    }

    /**
     * Get the information and timetable on a specific week
     * @param school The name of the school
     * @param sClass The class name
     * @param week Relative weeknumber (-1,0,1...)
     * @return Timetable for the class in the specified week
     */
    public JSONObject getClassInfo(String school, String sClass, int week)
    {
        return classApiCall("/" + school + "/class/" + sClass + "/" + week);
    }

    /**
     * Get all weeks that are available for the class
     * @param school The name of the class
     * @param sClass The class name
     * @return An array with all available weeknumbers
     */
    public JSONArray getAvailableWeeks(String school, String sClass)
    {
        String apiResult = apiCall("/" + school + "/class/" + sClass + "/listWeeks");

        JSONArray result = null;
        try {
            JSONObject json = new JSONObject(apiResult);
            result = json.getJSONArray("schools");
        } catch(Exception e){
            System.out.println("Json error: "+e.getMessage());
            return null;
        }
        schools = result;
        return result;
    }

    /**
     * Call for the class data
     * @param apiUrl Url for the api
     * @return The requested class data
     */
    private JSONObject classApiCall(String apiUrl)
    {
        String apiResult = apiCall(apiUrl);
        JSONObject result = null;
        try {
            result = new JSONObject(apiResult);
        } catch(Exception e){
            System.out.println("Json error: "+e.getMessage());
            return null;
        }
        return result;
    }

    /**
     * Performs the actual call to the api
     * @param type defines which api data should be called
     * @return The string returned from the api
     */
    private String apiCall(String type)
    {
        InputStream inputStream = null;
        String result = "";
        try {

            // create Http Connection
            URL urlObj = new URL(url+type);
            HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();
            try {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());

                // convert inputstream to string
                if(inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";
            } finally {
                //Always close the connection
                urlConnection.disconnect();
            }

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    /**
     * Converts the InputStream from the api to a string
     *
     * @param inputStream Input from the api
     * @return A string returned from the api
     * @throws IOException
     */
    private String convertInputStreamToString(InputStream inputStream) throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();

        return result;
    }

}

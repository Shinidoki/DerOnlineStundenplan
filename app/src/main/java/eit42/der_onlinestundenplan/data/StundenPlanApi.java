package eit42.der_onlinestundenplan.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.util.Calendar;
import java.util.GregorianCalendar;


public class StundenPlanApi {
    private static final String url = "http://beta.der-onlinestundenplan.de/api/v1/school";
    private static final String userAgent = "OnlineStundenplan/Android_App_v1.0";

    private JSONArray schools = null;
    private JSONObject classes = null;
    private Context context;

    public StundenPlanApi(Context context) {
        this.context = context;
    }

    /**
     * Gets all schools from the api if not already fetched
     *
     * @return JSONArray of all Schools
     */
    public JSONArray getSchools() {
        if (schools != null) {
            return schools;
        }

        String apiResult = apiCall("");

        JSONArray result;
        try {
            JSONObject json = new JSONObject(apiResult);
            result = json.getJSONArray("schools");
        } catch (Exception e) {
            Log.d("API/JSON Schools", "Json error: " + e.getMessage());
            return null;
        }
        schools = result;
        saveSchools(result);
        return result;
    }

    private boolean schoolExists(String schoolName)
    {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                SchoolContract.SchoolEntry.COLUMN_NAME_S_NAME
        };

        String sortOrder = SchoolContract.SchoolEntry.COLUMN_NAME_S_NAME + " ASC";
        String where = SchoolContract.SchoolEntry.COLUMN_NAME_S_NAME + " = \"" + schoolName + "\" ";

        int count;
        Cursor c = null;

        try {
            c = db.query(
                    SchoolContract.SchoolEntry.TABLE_NAME,
                    projection,
                    where, null, null, null,
                    sortOrder
            );

            count = c.getCount();

        } catch (SQLiteException e) {
            Log.d("SQL", "Fehler beim query der Schulen: " + e.getLocalizedMessage());
            count = 0;
            if (c != null) {
                c.close();
            }
        }

        return count != 0;
    }

    private boolean classExists(String schoolName, String className)
    {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                ClassContract.ClassEntry.COLUMN_NAME_C_NAME
        };

        String sortOrder = ClassContract.ClassEntry.COLUMN_NAME_C_NAME + " ASC";
        String where = ClassContract.ClassEntry.COLUMN_NAME_C_NAME + " = \"" + className + "\" " +
                        "AND "+ ClassContract.ClassEntry.COLUMN_NAME_C_SCHOOL + " = \"" + schoolName + "\" ";

        int count;
        Cursor c = null;

        try {
            c = db.query(
                    ClassContract.ClassEntry.TABLE_NAME,
                    projection,
                    where, null, null, null,
                    sortOrder
            );

            count = c.getCount();

        } catch (SQLiteException e) {
            Log.d("SQL", "Fehler beim query der Schulen: " + e.getLocalizedMessage());
            count = 0;
            if (c != null) {
                c.close();
            }
        }

        return count != 0;
    }

    /**
     * Save the schools in the database
     *
     * @param schools which schould be saved
     */
    private void saveSchools(JSONArray schools) {
        int len = schools.length();
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (int i = 0; i < len; i++) {
            try {
                JSONObject school = schools.getJSONObject(i);
                if(!schoolExists(school.getString("name"))){
                    ContentValues values = new ContentValues();
                    values.put(SchoolContract.SchoolEntry.COLUMN_NAME_S_NAME, school.getString("name"));
                    values.put(SchoolContract.SchoolEntry.COLUMN_NAME_S_CITY, school.getString("city"));
                    values.put(SchoolContract.SchoolEntry.COLUMN_NAME_S_WEBSITE, school.getString("website"));

                    db.insert(
                            SchoolContract.SchoolEntry.TABLE_NAME,
                            null,
                            values
                    );
                }
            } catch (Exception e) {
                Log.d("SQL/API School", "Fehler beim speichern der Schule in der Datenbank: " + e.getMessage());
            }
        }
        db.close();
    }

    /**
     * Get all Schools as an String array
     *
     * @return String array of all schools
     */
    public String[] getSchoolsArray() {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                SchoolContract.SchoolEntry.COLUMN_NAME_S_NAME
        };

        String sortOrder = SchoolContract.SchoolEntry.COLUMN_NAME_S_NAME + " ASC";

        int count;
        Cursor c = null;

        try {
            c = db.query(
                    SchoolContract.SchoolEntry.TABLE_NAME,
                    projection,
                    null, null, null, null,
                    sortOrder
            );

            count = c.getCount();

        } catch (SQLiteException e) {
            Log.d("SQL", "Fehler beim query der Schulen: " + e.getLocalizedMessage());
            count = 0;
            if (c != null) {
                c.close();
            }
        }

        if (count == 0) {
            if (c != null) {
                c.close();
            }
            JSONArray schoolArray = getSchools();
            int len = schoolArray.length();
            String[] result = new String[len];

            for (int i = 0; i < len; i++) {
                try {
                    result[i] = schoolArray.getJSONObject(i).getString("name");
                } catch (Exception e) {
                    Log.d("API School", "Fehler beim konvertieren der Schulen ins Array format: " + e.getMessage());
                }
            }
            return result;
        } else {
            c.moveToFirst();
            String[] result = new String[count];
            for (int i = 0; i < count; i++) {
                result[i] = c.getString(c.getColumnIndex(SchoolContract.SchoolEntry.COLUMN_NAME_S_NAME));
                c.move(1);
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
    public JSONObject getSchoolInfo(String school) {
        String apiResult = apiCall("/" + school);

        JSONObject result;
        try {
            result = new JSONObject(apiResult);
        } catch (Exception e) {
            Log.d("API/SchoolInfo", "Json error: " + e.getMessage());
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
    public JSONArray getClasses(String school) {
        if (classes == null) {
            classes = new JSONObject();
        }
        if (classes.has(school)) {
            try {
                return classes.getJSONArray(school);
            } catch (Exception e) {
                Log.d("API/Classes", "Fehler beim holen der Klassen: " + e.getMessage());
                return null;
            }
        }

        String apiResult = apiCall("/" + school + "/class");

        JSONObject result;
        JSONArray arrayResult;
        try {
            result = new JSONObject(apiResult);

            arrayResult = result.getJSONArray("classes");

            classes.put(school, arrayResult);

        } catch (Exception e) {
            Log.d("API/Classes", "Json error: " + e.getMessage());
            return null;
        }
        saveClasses(arrayResult, school);
        return arrayResult;
    }

    /**
     * Save the classes in the database
     *
     * @param classes which schould be saved
     * @param school  to which the class belongs
     */
    private void saveClasses(JSONArray classes, String school) {
        int len = classes.length();
        DBHelper dbHelper = DBHelper.getInstance(context);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        for (int i = 0; i < len; i++) {
            try {
                String sClass = classes.getString(i);
                if(!classExists(school,sClass)){
                    ContentValues values = new ContentValues();
                    values.put(ClassContract.ClassEntry.COLUMN_NAME_C_NAME, sClass);
                    values.put(ClassContract.ClassEntry.COLUMN_NAME_C_SCHOOL, school);
                    db.insert(
                            ClassContract.ClassEntry.TABLE_NAME,
                            null,
                            values
                    );
                }
            } catch (Exception e) {
                Log.d("API/Classes", "Fehler beim speichern der Schule in der Datenbank: " + e.getMessage());
            }
        }
        db.close();
    }

    /**
     * Get all classes as a String array
     *
     * @return String array of all classes
     */
    public String[] getClassesArray(String school) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                ClassContract.ClassEntry.COLUMN_NAME_C_NAME
        };

        String sortOrder = ClassContract.ClassEntry.COLUMN_NAME_C_NAME + " ASC";
        int count;
        Cursor c = null;
        try {

            c = db.query(
                    ClassContract.ClassEntry.TABLE_NAME,
                    projection,
                    null, null, null, null,
                    sortOrder
            );

            count = c.getCount();
        } catch (SQLiteException e) {
            Log.d("SQL", "Fehler beim query der Klassen: " + e.getLocalizedMessage());
            count = 0;
            if (c != null) {
                c.close();
            }
        }

        if (count == 0) {
            if (c != null) {
                c.close();
            }
            JSONArray classArray = getClasses(school);
            if(classArray == null){
                return new String[0];
            }
            int len = classArray.length();
            String[] result = new String[len];

            for (int i = 0; i < len; i++) {
                try {
                    result[i] = classArray.getString(i);
                } catch (Exception e) {
                    Log.d("API/Schools", "Fehler beim konvertieren der Schulen ins Array format: " + e.getMessage());
                }
            }
            return result;
        } else {
            c.moveToFirst();
            String[] result = new String[count];
            for (int i = 0; i < count; i++) {
                result[i] = c.getString(c.getColumnIndex(ClassContract.ClassEntry.COLUMN_NAME_C_NAME));
                c.move(1);
            }
            c.close();
            return result;
        }
    }


    /**
     * Get more information about a class
     *
     * @param school The name of the school
     * @param sClass The class name
     * @return Information about the class (currentWeek, Timetable, lastUpdated)
     */
    public JSONObject getClassInfo(String school, String sClass) {
        Calendar calendar = new GregorianCalendar();
        int currentWeek = calendar.get(GregorianCalendar.WEEK_OF_YEAR);
        return getClassInfo(school, sClass, currentWeek);
    }

    /**
     * Get the information and timetable on a specific week
     *
     * @param school The name of the school
     * @param sClass The class name
     * @param week   Weeknumber (8,12...)
     * @return Timetable for the class in the specified week
     */
    public JSONObject getClassInfo(String school, String sClass, int week) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                TimeTableContract.TimeEntry.COLUMN_NAME_T_DATA
        };

        String sortOrder = TimeTableContract.TimeEntry.COLUMN_NAME_T_WEEK + " ASC";
        String where = TimeTableContract.TimeEntry.COLUMN_NAME_T_SCHOOL + " = \"" + school + "\" " +
                "AND " + TimeTableContract.TimeEntry.COLUMN_NAME_T_CLASS + " = \"" + sClass + "\"" +
                "AND " + TimeTableContract.TimeEntry.COLUMN_NAME_T_WEEK + " = " + week;
        int count;
        Cursor c = null;
        try {

            c = db.query(
                    TimeTableContract.TimeEntry.TABLE_NAME,
                    projection,
                    where, null, null, null,
                    sortOrder
            );

            count = c.getCount();
        } catch (SQLiteException e) {
            Log.d("SQL", "Fehler beim query des Stundenplans: " + e.getLocalizedMessage());
            count = 0;
            if (c != null) {
                c.close();
            }
        }

        if (count == 0 || isOnline()) {
            if (c != null) {
                c.close();
            }
            JSONObject timeTable = classApiCall("/" + school + "/class/" + sClass + "/fixedWeek/" + week);
            if (timeTable != null) {
                timeTable.remove("success");
                saveTimeTable(sClass, school, week, timeTable);
            }

            return timeTable;
        } else {
            c.moveToFirst();
            try {
                return new JSONObject(c.getString(c.getColumnIndex(TimeTableContract.TimeEntry.COLUMN_NAME_T_DATA)));
            } catch (Exception e) {
                Log.d("SQL/API", "Fehler beim erstellen des Stundenplan JSON aus Datenbank");
                return null;
            }
        }
    }

    /**
     * Save a time table for a class
     *
     * @param sClass Class of the timetable
     * @param school School of the class
     * @param week   Calenderweek of the timetable
     * @param data   JSON data of the timetable
     */
    private void saveTimeTable(String sClass, String school, int week, JSONObject data) {
        DBHelper dbHelper = DBHelper.getInstance(context);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            String where = TimeTableContract.TimeEntry.COLUMN_NAME_T_SCHOOL + " = \"" + school + "\" " +
                    "AND " + TimeTableContract.TimeEntry.COLUMN_NAME_T_CLASS + " = \"" + sClass + "\"" +
                    "AND " + TimeTableContract.TimeEntry.COLUMN_NAME_T_WEEK + " = " + week;
            db.delete(
                    TimeTableContract.TimeEntry.TABLE_NAME,
                    where,
                    null
            );
            ContentValues values = new ContentValues();
            values.put(TimeTableContract.TimeEntry.COLUMN_NAME_T_CLASS, sClass);
            values.put(TimeTableContract.TimeEntry.COLUMN_NAME_T_SCHOOL, school);
            values.put(TimeTableContract.TimeEntry.COLUMN_NAME_T_WEEK, week);
            values.put(TimeTableContract.TimeEntry.COLUMN_NAME_T_UPDATED, data.getString("last_updated"));
            values.put(TimeTableContract.TimeEntry.COLUMN_NAME_T_DATA, data.toString());
            db.insert(
                    TimeTableContract.TimeEntry.TABLE_NAME,
                    null,
                    values
            );
        } catch (Exception e) {
            Log.d("API/Schools", "Fehler beim speichern der Schule in der Datenbank: " + e.getMessage());
        }

        db.close();
    }

    /**
     * Get all weeks that are available for the class
     *
     * @param school The name of the class
     * @param sClass The class name
     * @return An array with all available weeknumbers
     */
    public JSONArray getAvailableWeeks(String school, String sClass) {
        String apiResult = apiCall("/" + school + "/class/" + sClass + "/listWeeks");

        JSONArray result = null;
        try {
            JSONObject json = new JSONObject(apiResult);
            result = json.getJSONArray("schools");
        } catch (Exception e) {
            Log.d("API/AvailableWeeks", "Json error: " + e.getMessage());
            return null;
        }
        schools = result;
        return result;
    }

    /**
     * Call for the class data
     *
     * @param apiUrl Url for the api
     * @return The requested class data
     */
    private JSONObject classApiCall(String apiUrl) {
        String apiResult = apiCall(apiUrl);
        JSONObject result;
        try {
            result = new JSONObject(apiResult);
            if (!result.getString("success").equals("true")) {
                Log.d("APIResult", "Got no success from API: " + apiResult);
                return null;
            }
        } catch (Exception e) {
            Log.d("APICall", "Json error: " + e.getMessage());
            return null;
        }
        return result;
    }

    /**
     * Performs the actual call to the api
     *
     * @param type defines which api data should be called
     * @return The string returned from the api
     */
    private String apiCall(String type) {
        InputStream inputStream;
        String result = "";
        try {
            // create Http Connection
            URL urlObj = new URL(url + type);
            HttpURLConnection urlConnection = (HttpURLConnection) urlObj.openConnection();
            urlConnection.setRequestProperty("User-Agent",userAgent);
            try {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());

                // convert inputstream to string
                result = convertInputStreamToString(inputStream);
            } finally {
                //Always close the connection
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage(),e);
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
    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();

        return result;
    }

    /**
     * Checks if there is a network connection
     *
     * @return true if there is a network connection
     */
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}

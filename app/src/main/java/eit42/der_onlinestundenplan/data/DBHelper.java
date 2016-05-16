package eit42.der_onlinestundenplan.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Stundenplan.db";
    private String[] createEntries = new String[3];
    private String[] deleteEntries = new String[3];

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        createEntries[0] = SchoolContract.SQL_CREATE_ENTRIES;
        createEntries[1] = ClassContract.SQL_CREATE_ENTRIES;
        deleteEntries[0] = SchoolContract.SQL_DELETE_ENTRIES;
        deleteEntries[1] = ClassContract.SQL_DELETE_ENTRIES;
        createEntries[2] = TimeTableContract.SQL_CREATE_ENTRIES;
        deleteEntries[2] = TimeTableContract.SQL_DELETE_ENTRIES;
    }

    public void onCreate(SQLiteDatabase db) {
        for (String create: createEntries) {
            db.execSQL(create);
        }
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        for (String delete: deleteEntries) {
            db.execSQL(delete);
        }
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

package eit42.der_onlinestundenplan.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Stundenplan.db";
    private String createEntries, deleteEntries;

    public DBHelper(Context context, String create, String delete) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        createEntries = create;
        deleteEntries = delete;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createEntries);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(deleteEntries);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}

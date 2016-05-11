package eit42.der_onlinestundenplan.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class SchoolContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public SchoolContract() {}

    /* Inner class that defines the table contents */
    public static abstract class SchoolEntry implements BaseColumns {
        public static final String TABLE_NAME = "schools";
        public static final String COLUMN_NAME_S_NAME = "s_name";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SchoolEntry.TABLE_NAME + " (" +
                    SchoolEntry._ID + " INTEGER PRIMARY KEY," +
                    SchoolEntry.COLUMN_NAME_S_NAME + TEXT_TYPE + COMMA_SEP +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SchoolEntry.TABLE_NAME;

    public class SchoolDBHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Stundenplan.db";

        public SchoolDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

}

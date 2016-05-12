package eit42.der_onlinestundenplan.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class SchoolContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it a private constructor.
    private SchoolContract() {}

    /* Inner class that defines the table contents */
    public static abstract class SchoolEntry implements BaseColumns {
        public static final String TABLE_NAME = "schools";
        public static final String COLUMN_NAME_S_NAME = "s_name";
        public static final String COLUMN_NAME_S_CITY = "s_city";
        public static final String COLUMN_NAME_S_WEBSITE = "s_website";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + SchoolEntry.TABLE_NAME + " (" +
                    SchoolEntry._ID + " INTEGER PRIMARY KEY," +
                    SchoolEntry.COLUMN_NAME_S_NAME + TEXT_TYPE + COMMA_SEP +
                    SchoolEntry.COLUMN_NAME_S_CITY + TEXT_TYPE + COMMA_SEP +
                    SchoolEntry.COLUMN_NAME_S_WEBSITE + TEXT_TYPE +
            " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SchoolEntry.TABLE_NAME;


}

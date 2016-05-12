package eit42.der_onlinestundenplan.data;

import android.provider.BaseColumns;

public final class ClassContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it a private constructor.
    private ClassContract() {}

    /* Inner class that defines the table contents */
    public static abstract class ClassEntry implements BaseColumns {
        public static final String TABLE_NAME = "classes";
        public static final String COLUMN_NAME_S_NAME = "c_name";
        public static final String COLUMN_NAME_S_CITY = "s_city";
        public static final String COLUMN_NAME_S_WEBSITE = "s_website";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ClassEntry.TABLE_NAME + " (" +
                    ClassEntry._ID + " INTEGER PRIMARY KEY," +
                    ClassEntry.COLUMN_NAME_S_NAME + TEXT_TYPE + COMMA_SEP +
                    ClassEntry.COLUMN_NAME_S_CITY + TEXT_TYPE + COMMA_SEP +
                    ClassEntry.COLUMN_NAME_S_WEBSITE + TEXT_TYPE +
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ClassEntry.TABLE_NAME;


}

package eit42.der_onlinestundenplan.data;

import android.provider.BaseColumns;

public final class ClassContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it a private constructor.
    private ClassContract() {}

    /* Inner class that defines the table contents */
    public static abstract class ClassEntry implements BaseColumns {
        public static final String TABLE_NAME = "classes";
        public static final String COLUMN_NAME_C_NAME = "c_name";
        public static final String COLUMN_NAME_C_SCHOOL = "c_school";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    //TODO Mit foreign keys arbeiten
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + ClassEntry.TABLE_NAME + " (" +
                    ClassEntry._ID + " INTEGER PRIMARY KEY," +
                    ClassEntry.COLUMN_NAME_C_NAME + TEXT_TYPE + COMMA_SEP +
                    ClassEntry.COLUMN_NAME_C_SCHOOL + TEXT_TYPE + COMMA_SEP +
                    " CONSTRAINT uc_nameSchool UNIQUE ("+ClassEntry.COLUMN_NAME_C_NAME + COMMA_SEP + ClassEntry.COLUMN_NAME_C_SCHOOL+") "+
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ClassEntry.TABLE_NAME;


}

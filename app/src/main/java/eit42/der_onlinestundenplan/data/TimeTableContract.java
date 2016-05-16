package eit42.der_onlinestundenplan.data;

import android.provider.BaseColumns;

public final class TimeTableContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it a private constructor.
    private TimeTableContract() {}

    /* Inner class that defines the table contents */
    public static abstract class TimeEntry implements BaseColumns {
        public static final String TABLE_NAME = "timetables";
        public static final String COLUMN_NAME_T_SCHOOL = "t_school";
        public static final String COLUMN_NAME_T_CLASS = "t_class";
        public static final String COLUMN_NAME_T_WEEK = "t_week";
        public static final String COLUMN_NAME_T_UPDATED = "t_updated";
        public static final String COLUMN_NAME_T_DATA = "t_data";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INT";
    private static final String DATE_TYPE = " DATETIME";
    private static final String COMMA_SEP = ",";
    //TODO Mit foreign keys arbeiten
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + TimeEntry.TABLE_NAME + " (" +
                    TimeEntry._ID + " INTEGER PRIMARY KEY," +
                    TimeEntry.COLUMN_NAME_T_SCHOOL + TEXT_TYPE + COMMA_SEP +
                    TimeEntry.COLUMN_NAME_T_CLASS + TEXT_TYPE + COMMA_SEP +
                    TimeEntry.COLUMN_NAME_T_WEEK + INT_TYPE + COMMA_SEP +
                    TimeEntry.COLUMN_NAME_T_UPDATED + DATE_TYPE + COMMA_SEP +
                    TimeEntry.COLUMN_NAME_T_DATA + TEXT_TYPE +
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TimeEntry.TABLE_NAME;


}

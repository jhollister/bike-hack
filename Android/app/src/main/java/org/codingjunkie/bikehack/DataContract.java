package org.codingjunkie.bikehack;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by john on 11/30/15.
 */
public class DataContract {
    private static final String TAG = DataContract.class.getSimpleName();

    public static final String DELIM = ",";
    public static final String NAME_TYPE = " TEXT";
    public static final String ID_TYPE = " INTEGER";
    public static final String VALUE_TYPE = " TEXT";
    public static final String TINY_INT = " UNSIGNED TINYINT";

    // Default constructor
    private DataContract() {}

    // Table names
    public static final String[] tableNames = {Settings.TABLE_NAME};

    // Trigger names
    public static final String[] triggerNames = {};

    // Table column names
    public static abstract class Settings implements BaseColumns {
        public static final String TABLE_NAME = "settings";
        public static final String CN_NAME = "name";
        public static final String CN_VALUE = "value";
    }

    /*
    Strings for creating our tables
     */
    public static final String CREATE_SETTINGS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + Settings.TABLE_NAME + " (\n" +
                    Settings._ID + ID_TYPE + " PRIMARY KEY AUTOINCREMENT" + DELIM +
                    Settings.CN_NAME + NAME_TYPE + " UNIQUE COLLATE NOCASE" + DELIM +
                    Settings.CN_VALUE + VALUE_TYPE + " );";

    /*
    Methods for destroying our tables and triggers
     */
    public static void destroyTables(SQLiteDatabase db) {
        for (String table : tableNames) {
            db.execSQL("DROP TABLE IF EXISTS " + table);
            Log.d("DataContract", "DROP TABLE IF EXISTS " + table);
        }
    }

    public static void destroyTriggers(SQLiteDatabase db) {
        for (String trigger : triggerNames ) {
            db.execSQL("DROP TRIGGER IF EXISTS " + trigger);
            Log.d(TAG, "DROP TRIGGER IF EXISTS  " + trigger);
        }
    }
}

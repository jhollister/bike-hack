package org.codingjunkie.bikehack;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by john on 11/30/15.
 */
public class DataHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "brightwheels.db";
    private static DataHelper instance = null;

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DataContract.CREATE_SETTINGS_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DataContract.destroyTables(db);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // Should pass in application context to get one instance across the app.
    static public DataHelper getInstance(Context context) {
        try {
            if (instance == null) {
                instance = new DataHelper(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return instance;
        }
    }
}

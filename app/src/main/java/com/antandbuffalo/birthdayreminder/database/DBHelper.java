package com.antandbuffalo.birthdayreminder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.settings.SettingsModel;

import org.json.JSONObject;

import java.util.List;

import org.json.JSONObject;

public final class DBHelper extends SQLiteOpenHelper {

    private static DBHelper dbHelperInstance = null;

    // http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
    // http://www.vogella.com/articles/AndroidSQLite/article.html

    //refence for date
    //http://stackoverflow.com/questions/7363112/best-way-to-work-with-dates-in-android-sqlite

    private DBHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    public static DBHelper createInstance(Context context) {
        if(dbHelperInstance == null) {
            dbHelperInstance = new DBHelper(context);
        }
        return dbHelperInstance;
    }

    public static DBHelper getInstace() {
        if(dbHelperInstance == null) {
            Log.e("DBHeloper", "Instance not created");
        }
        return dbHelperInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String CREATE_DOB_TABLE = "CREATE TABLE " + Constants.TABLE_DATE_OF_BIRTH
                + "(" + Constants.COLUMN_DOB_ID + " INTEGER PRIMARY KEY autoincrement, "
                + Constants.COLUMN_DOB_NAME + " TEXT NOT NULL, "
                + Constants.COLUMN_DOB_DATE + " DATE NOT NULL, "
                + Constants.COLUMN_DOB_EXTRA + " TEXT, "
                + Constants.COLUMN_DOB_OPTIONAL_YEAR + " INTEGER"
                +")";
        System.out.println("create query -- " + CREATE_DOB_TABLE);


        String CREATE_OPTION_TABLE = "CREATE TABLE " + Constants.TABLE_OPTIONS + "( "
                + Constants.COLUMN_OPTION_CODE + " TEXT PRIMARY KEY, "
                + Constants.COLUMN_OPTION_TITLE + " TEXT, "
                + Constants.COLUMN_OPTION_SUBTITLE + " TEXT, "
                + Constants.COLUMN_OPTION_UPDATED_ON + " DATE, "
                + Constants.COLUMN_OPTION_EXTRA + " TEXT, "
                + Constants.COLUMN_OPTION_SNO+ " INTEGER"
                +")";
        System.out.println("create option table query -- " + CREATE_OPTION_TABLE);

        db.execSQL(CREATE_DOB_TABLE);
        db.execSQL(CREATE_OPTION_TABLE);

        //inserting default values
        //OptionsDBHelper.insertDefaultValues(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        switch (oldVersion) {
            case 1: {
                String addSNOColumn = "ALTER TABLE " + Constants.TABLE_OPTIONS + " ADD COLUMN " + Constants.COLUMN_OPTION_SNO + " INTEGER";
                db.execSQL(addSNOColumn);
                OptionsDBHelper.updateSNO(db);
            }
            case 2: {
                String addOptionalYearQuery = "ALTER TABLE " + Constants.TABLE_DATE_OF_BIRTH + " ADD COLUMN " + Constants.COLUMN_DOB_OPTIONAL_YEAR + " INTEGER DEFAULT 0";
                db.execSQL(addOptionalYearQuery);
            }
        }

//        if(oldVersion == 1 && newVersion == Constants.DATABASE_VERSION) {
//            String addSNOColumn = "ALTER TABLE " + Constants.TABLE_OPTIONS + " ADD COLUMN " + Constants.COLUMN_OPTION_SNO + " INTEGER";
//            db.execSQL(addSNOColumn);
//            OptionsDBHelper.updateSNO(db);
//        }
//        if(oldVersion == 2 && newVersion == Constants.DATABASE_VERSION) {
//            String addOptionalYearQuery = "ALTER TABLE " + Constants.TABLE_DATE_OF_BIRTH + " ADD COLUMN " + Constants.COLUMN_DOB_OPTIONAL_YEAR + " INTEGER";
//            db.execSQL(addOptionalYearQuery);
//        }

        // Drop older table if existed
//        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_DATE_OF_BIRTH);
//        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_OPTIONS);
//        // Create tables again
//        onCreate(db);
    }

    public static int deleteAll(String tableName) {
        SQLiteDatabase db = DBHelper.getInstace().getWritableDatabase();
        int numberOfRowsDeleted = db.delete(tableName, "1", null);
        db.close();
        Log.i("delete all", "Table name - " + tableName + " - return value - " + numberOfRowsDeleted);
        return numberOfRowsDeleted;
    }
    public static long getNumberOfRows(String tableName) {
        SQLiteDatabase db = DBHelper.getInstace().getReadableDatabase();
        long cnt  = DatabaseUtils.queryNumEntries(db, tableName);
        db.close();
        return cnt;
    }
}
package com.antandbuffalo.birthdayreminder.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.antandbuffalo.birthdayreminder.Constants;

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
        String CREATE_DOB_TABLE = "CREATE TABLE " + Constants.TABLE_DATE_OF_BIRTH + "(" + Constants.COLUMN_DOB_ID
                + " INTEGER PRIMARY KEY autoincrement," + Constants.COLUMN_DOB_NAME + " TEXT NOT NULL,"
                + Constants.COLUMN_DOB_DATE + " DATE NOT NULL" +")";
        System.out.println("create query -- " + CREATE_DOB_TABLE);

        String CREATE_OPTION_TABLE = "CREATE TABLE " + Constants.TABLE_OPTIONS + "( "
                + Constants.COLUMN_OPTION_CODE + " TEXT PRIMARY KEY, "
                + Constants.COLUMN_OPTION_TITLE + " TEXT, "
                + Constants.COLUMN_OPTION_SUBTITLE + " TEXT, "
                + Constants.COLUMN_OPTION_UPDATED_ON + " DATE"
                +")";
        System.out.println("create option table query -- " + CREATE_OPTION_TABLE);

        db.execSQL(CREATE_DOB_TABLE);
        db.execSQL(CREATE_OPTION_TABLE);

        //inserting default values
        OptionsDBHelper.insertDefaultValues(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // TODO Auto-generated method stub
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_DATE_OF_BIRTH);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_OPTIONS);

        // Create tables again
        onCreate(db);
    }
}
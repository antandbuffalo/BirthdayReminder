package com.antandbuffalo.birthdayreminder.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DateOfBirth;

public final class DBHelper extends SQLiteOpenHelper {

    private static DBHelper dbHelperInstance = null;

    // http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
    // http://www.vogella.com/articles/AndroidSQLite/article.html

    //refence for date
    //http://stackoverflow.com/questions/7363112/best-way-to-work-with-dates-in-android-sqlite

    private DBHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    public static void createInstance(Context context) {
        if(dbHelperInstance == null) {
            dbHelperInstance = new DBHelper(context);
        }
    }

    public static DBHelper getInstace() {
        return dbHelperInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String CREATE_DOB_TABLE = "CREATE TABLE " + Constants.TABLE_DOB + "(" + Constants.KEY_DOB_ID
                + " INTEGER PRIMARY KEY autoincrement," + Constants.KEY_DOB_NAME + " TEXT NOT NULL,"
                + Constants.KEY_DOB_DOB + " DATE NOT NULL" +")";
        System.out.println("create query -- " + CREATE_DOB_TABLE);
        db.execSQL(CREATE_DOB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // TODO Auto-generated method stub
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_DOB);

        // Create tables again
        onCreate(db);
    }
}
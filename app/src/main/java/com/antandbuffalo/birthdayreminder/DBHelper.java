package com.antandbuffalo.birthdayreminder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "BirthdayReminder";

    // Contacts table name
    private static final String TABLE_DOB = "UserDob";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DOB = "dob";
    private static final String KEY_YEAR = "year";
    private static final String KEY_EXTRA1 = "extra1";
    private static final String KEY_EXTRA2 = "extra2";
    private static final String KEY_EXTRA3 = "extra3";

    // http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
    // http://www.vogella.com/articles/AndroidSQLite/article.html

    //refence for date
    //http://stackoverflow.com/questions/7363112/best-way-to-work-with-dates-in-android-sqlite

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        String CREATE_DOB_TABLE = "CREATE TABLE " + TABLE_DOB + "(" + KEY_ID
                + " INTEGER PRIMARY KEY autoincrement," + KEY_NAME + " TEXT,"
                + KEY_DOB + " INT," + KEY_YEAR + " INT," + KEY_EXTRA1 + " TEXT," + KEY_EXTRA2 + " TEXT," + KEY_EXTRA3 + " TEXT" +")";
        System.out.println("create query -- " + CREATE_DOB_TABLE);
        db.execSQL(CREATE_DOB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // TODO Auto-generated method stub
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOB);

        // Create tables again
        onCreate(db);
    }

   /* public void addDOB(DateOfBirth dateOfBirth) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, dateOfBirth.getName()); // Contact Name
        values.put(KEY_DOB, dateOfBirth.getDob()); // date of birth - 2000
        values.put(KEY_ORIGINAL_YEAR, dateOfBirth.getOriginalYear()); // date of birth original

        // Inserting Row
        db.insert(TABLE_DOB, null, values);
        db.close(); // Closing database connection
    }

    public void updateDOB(DateOfBirth dateOfBirth) {
        SQLiteDatabase db = this.getWritableDatabase();

//		String selectionQuery;
//		selectionQuery = String.format("update %s set %s = '%s', %s = '%s' where %s = '%s'", TABLE_DOB,
//					KEY_NAME, dateOfBirth.getName(), KEY_DOB, dateOfBirth.getDob(), KEY_ID, dateOfBirth.getId());
//		System.out.println("query--" + selectionQuery);
//		db.rawQuery(selectionQuery, null);

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, dateOfBirth.getName()); // Contact Name
        values.put(KEY_DOB, dateOfBirth.getDob()); // date of birth - 2000
        values.put(KEY_ORIGINAL_YEAR, dateOfBirth.getOriginalYear()); // date of birth original

        String where = "id = ?";
        db.update(TABLE_DOB, values, where, new String[]{String.valueOf(dateOfBirth.getId())});

        db.close(); // Closing database connection
    }*/


    public List<DateOfBirth> getDOBList(String givenDOB) {
        List<DateOfBirth> dobList = new ArrayList<DateOfBirth>();
        // Select All Query
        String selectionQuery;
        if (givenDOB == null) {
            selectionQuery = String.format("select %s, %s, %s, %s from  %s",
                    KEY_ID, KEY_NAME, KEY_DOB, KEY_YEAR, TABLE_DOB);
        }
        else {
            selectionQuery = String.format(
                    "select %s, %s, %s, %s from %s where %s like '%s'", KEY_ID,
                    KEY_NAME, KEY_DOB, KEY_YEAR, TABLE_DOB, KEY_DOB, givenDOB);
        }
        System.out.println("query--" + selectionQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectionQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DateOfBirth dateOfBirth = new DateOfBirth();
                dateOfBirth.setId(Integer.parseInt(cursor.getString(0)));
                String nameWithSpace = cursor.getString(1);
                dateOfBirth.setName(nameWithSpace);

                String dateString = cursor.getString(2);

                // Adding contact to list
                dobList.add(dateOfBirth);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return contact list
        return dobList;
    }

    public Boolean deleteRecordForTheId(int givenId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_DOB, KEY_ID + "=" + givenId, null) > 0;
    }

    public void deleteAllRecords()
    {
        SQLiteDatabase db = this.getWritableDatabase();

//		String selectionQuery;
//		selectionQuery = String.format("delete from %s", TABLE_DOB);
//		System.out.println("query--" + selectionQuery);
//		db.rawQuery(selectionQuery, null);

        System.out.println("del - " + db.delete(TABLE_DOB, null, null));
        db.close();
    }
}
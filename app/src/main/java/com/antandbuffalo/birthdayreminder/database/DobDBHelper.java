package com.antandbuffalo.birthdayreminder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by i677567 on 12/10/15.
 */
public class DobDBHelper {

    public static void addDOB() {
        DBHelper dbHelper = DBHelper.getInstace();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", "sample_today"); // Contact Name
        values.put("dob", Util.getStringFromDate(new Date(1444817046891L))); // date of birth - 2000
        db.insert("Dob", null, values); // Inserting Row

        values.put("name", "sample_last_year"); // Contact Name
        values.put("dob", Util.getStringFromDate(new Date(1418342400000L))); // date of birth - 2000
        db.insert("Dob", null, values); // Inserting Row

        values.put("name", "sample_this_year"); // Contact Name
        values.put("dob", Util.getStringFromDate(new Date(1432512000000L))); // date of birth - 2000
        db.insert("Dob", null, values); // Inserting Row


        db.close(); // Closing database connection
    }

    public static void getAll() {
       List<DateOfBirth> dobList = new ArrayList<DateOfBirth>();
        // Select All Query
        String selectionQuery;
        //selectionQuery = String.format("select %s, %s, %s from  %s ORDER BY CAST (strftime('%j', %s) AS INTEGER",
                    //Constants.COLUMN_DOB_ID, Constants.COLUMN_DOB_NAME, Constants.COLUMN_DOB_DATE, Constants.TABLE_DATE_OF_BIRTH, Constants.COLUMN_DOB_DATE);

        selectionQuery = "select " + Constants.COLUMN_DOB_ID + ", "
                + Constants.COLUMN_DOB_NAME + ", "
                + Constants.COLUMN_DOB_DATE + " from "
                + Constants.TABLE_DATE_OF_BIRTH + " ORDER BY CAST (strftime('%j', "
                + Constants.COLUMN_DOB_DATE + ") AS INTEGER)";

        System.out.println("query--" + selectionQuery);
        SQLiteDatabase db = DBHelper.getInstace().getReadableDatabase();
        Cursor cursor = db.rawQuery(selectionQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Log.i("Cursor - ", cursor.getString(1));
                /*DateOfBirth dateOfBirth = new DateOfBirth();
                dateOfBirth.setDobId(cursor.getInt(0));
                dateOfBirth.setName(cursor.getString(1));
                dateOfBirth.setDob(new Date(cursor.getLong(2)));

                //dateOfBirth.setAge(Utility.getDiffYears(dateOfBirth.getDobDate(), dateOfBirth.getOriginalYear(), new Date()) + "");

                //dateOfBirth.setDisplayName(dateOfBirth.getDobDate().getDate() + " / " + (dateOfBirth.getDobDate().getMonth()+1) + " - " + dateOfBirth.getName());

                // Adding contact to list
                dobList.add(dateOfBirth);*/
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return contact list
    }

    public Boolean deleteRecordForTheId(int givenId) {
        SQLiteDatabase db = DBHelper.getInstace().getWritableDatabase();
        return db.delete(Constants.TABLE_DATE_OF_BIRTH, Constants.COLUMN_DOB_ID + "=" + givenId, null) > 0;
    }
}

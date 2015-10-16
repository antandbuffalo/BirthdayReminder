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
        values.put(Constants.COLUMN_DOB_NAME, "sample_today"); // Contact Name
        values.put(Constants.COLUMN_DOB_DATE, Util.getStringFromDate(new Date())); // date of birth - 2000
        db.insert(Constants.TABLE_DATE_OF_BIRTH, null, values); // Inserting Row

        values.put(Constants.COLUMN_DOB_NAME, "sample_last_year"); // Contact Name
        values.put(Constants.COLUMN_DOB_DATE, Util.getStringFromDate(new Date())); // date of birth - 2000
        db.insert(Constants.TABLE_DATE_OF_BIRTH, null, values); // Inserting Row

        values.put(Constants.COLUMN_DOB_NAME, "sample_this_year"); // Contact Name
        values.put(Constants.COLUMN_DOB_DATE, Util.getStringFromDate(new Date())); // date of birth - 2000
        db.insert(Constants.TABLE_DATE_OF_BIRTH, null, values); // Inserting Row


        db.close(); // Closing database connection
    }

    public static List selectAll() {
       List<DateOfBirth> dobList = new ArrayList<DateOfBirth>();
        // Select All Query
        String selectionQuery;

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
                DateOfBirth dateOfBirth = new DateOfBirth();
                dateOfBirth.setDobId(cursor.getLong(0));
                dateOfBirth.setName(cursor.getString(1));
                dateOfBirth.setDobDate(Util.getDateFromString(cursor.getString(2)));
                dateOfBirth.setAge(Util.getAge(dateOfBirth.getDobDate()));
                if(dateOfBirth.getAge() == 0) {
                    dateOfBirth.setDescription("Completing: " + (dateOfBirth.getAge() + 1) + " year");
                }
                else if(dateOfBirth.getAge() == 1) {
                    dateOfBirth.setDescription("Completing: " + (dateOfBirth.getAge() + 1) + " year");
                }
                else {
                    dateOfBirth.setDescription("Completing: " + (dateOfBirth.getAge() + 1) + " years");
                }

                Log.i("dob", dateOfBirth.getName() + dateOfBirth.getDescription());
                // Adding contact to list
                dobList.add(dateOfBirth);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dobList;
    }

    public Boolean deleteRecordForTheId(int givenId) {
        SQLiteDatabase db = DBHelper.getInstace().getWritableDatabase();
        return db.delete(Constants.TABLE_DATE_OF_BIRTH, Constants.COLUMN_DOB_ID + "=" + givenId, null) > 0;
    }
}

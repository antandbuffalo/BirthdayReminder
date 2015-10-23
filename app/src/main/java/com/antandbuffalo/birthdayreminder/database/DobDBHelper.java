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
        values.put(Constants.COLUMN_DOB_NAME, "Jeyabalaji"); // Contact Name
        values.put(Constants.COLUMN_DOB_DATE, Util.getStringFromDate(new Date(573321600000L))); // date of birth - 2000
        db.insert(Constants.TABLE_DATE_OF_BIRTH, null, values); // Inserting Row

        values.put(Constants.COLUMN_DOB_NAME, "Sivaraj"); // Contact Name
        values.put(Constants.COLUMN_DOB_DATE, Util.getStringFromDate(new Date(580492800000L))); // date of birth - 2000
        db.insert(Constants.TABLE_DATE_OF_BIRTH, null, values); // Inserting Row

        values.put(Constants.COLUMN_DOB_NAME, "Ram Gilma"); // Contact Name
        values.put(Constants.COLUMN_DOB_DATE, Util.getStringFromDate(new Date(574358400000L))); // date of birth - 2000
        db.insert(Constants.TABLE_DATE_OF_BIRTH, null, values); // Inserting Row


        db.close(); // Closing database connection
    }

    public static long insertDOB(DateOfBirth dateOfBirth) {
        DBHelper dbHelper = DBHelper.getInstace();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_DOB_NAME, dateOfBirth.getName()); // Contact Name
        values.put(Constants.COLUMN_DOB_DATE, Util.getStringFromDate(dateOfBirth.getDobDate())); // date of birth - 2000
        long returnValue = db.insert(Constants.TABLE_DATE_OF_BIRTH, null, values); // Inserting Row
        db.close();
        return returnValue;
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

        selectionQuery = "select " + Constants.COLUMN_DOB_ID + ", "
                + Constants.COLUMN_DOB_NAME + ", "
                + Constants.COLUMN_DOB_DATE + ", "
                + "case when day <= strftime('%j', 'now') then priority + 1 else priority end cp from"
                + " (select "
                + Constants.COLUMN_DOB_ID + ", "
                + Constants.COLUMN_DOB_NAME + ", "
                + Constants.COLUMN_DOB_DATE + ", "
                + "cast(strftime('%j', "
                + Constants.COLUMN_DOB_DATE + ") as int) as day, 0 as priority from "
                + Constants.TABLE_DATE_OF_BIRTH + ") order by cp, day";


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
                // Adding contact to list
                dobList.add(dateOfBirth);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dobList;
    }

    public static int deleteAll() {
        SQLiteDatabase db = DBHelper.getInstace().getWritableDatabase();

//		String selectionQuery;
//		selectionQuery = String.format("delete from %s", TABLE_DOB);
//		System.out.println("query--" + selectionQuery);
//		db.rawQuery(selectionQuery, null);
        int returnValue = db.delete(Constants.TABLE_DATE_OF_BIRTH, null, null);
        db.close();
        Log.i("delete all", returnValue + "");
        return returnValue;
    }

    public Boolean deleteRecordForTheId(int givenId) {
        SQLiteDatabase db = DBHelper.getInstace().getWritableDatabase();
        return db.delete(Constants.TABLE_DATE_OF_BIRTH, Constants.COLUMN_DOB_ID + "=" + givenId, null) > 0;
    }
}

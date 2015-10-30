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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by i677567 on 12/10/15.
 */
public class DobDBHelper {

    public static void addDOB() {
        DBHelper dbHelper = DBHelper.getInstace();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        List <DateOfBirth> dobs = new ArrayList<DateOfBirth>();
        DateOfBirth today = new DateOfBirth();
        today.setName("Today");
        today.setDobDate(new Date());

        DateOfBirth yesterday = new DateOfBirth();
        yesterday.setName("Tomorrow");
        Calendar cal = Util.getCalendar(new Date());
        cal.add(Calendar.DATE, 1);
        yesterday.setDobDate(cal.getTime());

        DateOfBirth tomorrow = new DateOfBirth();
        tomorrow.setName("Yesterday");
        cal.add(Calendar.DATE, -2);
        tomorrow.setDobDate(cal.getTime());

        dobs.add(today);
        dobs.add(yesterday);
        dobs.add(tomorrow);

        for (DateOfBirth dob : dobs) {
            Log.i("inserted ", insertDOB(dob) + "");
        }
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
        List<DateOfBirth> dobList = getDateOfBirthsromCursor(cursor);
        cursor.close();
        db.close();
        return dobList;
    }

    public static List selectTodayAndBelated() {
        String selectionQuery;
        selectionQuery = "select " + Constants.COLUMN_DOB_ID + ", "
                + Constants.COLUMN_DOB_NAME + ", "
                + Constants.COLUMN_DOB_DATE + ", "
                + "cast(strftime('%m%d', "
                + Constants.COLUMN_DOB_DATE + ") as int) as day from "
                + Constants.TABLE_DATE_OF_BIRTH + " where day >= (cast(strftime('%m%d', 'now') as int) - "
                + Constants.RECENT_DURATION + ") AND day <= cast(strftime('%m%d', 'now') as int) order by day desc";

        System.out.println("query today and belated --" + selectionQuery);
        SQLiteDatabase db = DBHelper.getInstace().getReadableDatabase();
        Cursor cursor = db.rawQuery(selectionQuery, null);
        List<DateOfBirth> dobList = getDateOfBirthsromCursor(cursor, "Completed");
        cursor.close();
        db.close();
        return dobList;
    }

    public static int getCountTodayAndBelated() {
        String selectionQuery;
        selectionQuery = "select count(" + Constants.COLUMN_DOB_ID + "), "
                + "cast(strftime('%m%d', "
                + Constants.COLUMN_DOB_DATE + ") as int) as day from "
                + Constants.TABLE_DATE_OF_BIRTH + " where day = cast(strftime('%m%d', 'now') as int)";

        System.out.println("query today and belated --" + selectionQuery);
        SQLiteDatabase db = DBHelper.getInstace().getReadableDatabase();
        Cursor cursor = db.rawQuery(selectionQuery, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public static List<DateOfBirth> getDateOfBirthsromCursor(Cursor cursor, String description) {
        List<DateOfBirth> dobList = new ArrayList<DateOfBirth>();
        if (cursor.moveToFirst()) {
            do {
                DateOfBirth dateOfBirth = new DateOfBirth();
                dateOfBirth.setDobId(cursor.getLong(0));
                dateOfBirth.setName(cursor.getString(1));
                dateOfBirth.setDobDate(Util.getDateFromString(cursor.getString(2)));
                dateOfBirth.setAge(Util.getAge(dateOfBirth.getDobDate()));
                if(dateOfBirth.getAge() == 0) {
                    dateOfBirth.setDescription(description + ": " + (dateOfBirth.getAge() + 1) + " year");
                }
                else {
                    dateOfBirth.setDescription(description + ": " + (dateOfBirth.getAge() + 1) + " years");
                }
                // Adding contact to list
                dobList.add(dateOfBirth);
            } while (cursor.moveToNext());
        }
        return dobList;
    }

    public static List<DateOfBirth> getDateOfBirthsromCursor(Cursor cursor) {
        return getDateOfBirthsromCursor(cursor, "Completing");
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

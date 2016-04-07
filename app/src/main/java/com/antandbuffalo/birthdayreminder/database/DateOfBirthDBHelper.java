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
public class DateOfBirthDBHelper {

    public static void addDOB() {
        DBHelper dbHelper = DBHelper.getInstace();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        List <DateOfBirth> dobs = new ArrayList<DateOfBirth>();
        DateOfBirth today = new DateOfBirth();
        today.setName("Dineshwar");
        today.setDobDate(new Date());

        Calendar cal = Util.getCalendar(new Date());

        DateOfBirth yesterday = new DateOfBirth();
        yesterday.setName("Jeyabalaji");
        cal.add(Calendar.DATE, -2);
        yesterday.setDobDate(cal.getTime());

        dobs.add(today);
        dobs.add(yesterday);

        for (DateOfBirth dob : dobs) {
            Log.i("inserted ", insertDOB(dob) + "");
        }
        db.close(); // Closing database connection
    }

    public static long addMember(DateOfBirth dob) {
        return insertDOB(dob);
    }

    public static boolean isUniqueDateOfBirth(DateOfBirth dob) {
        java.sql.Date sampleDate = new java.sql.Date(dob.getDobDate().getTime());
        String selectionQuery = "";
        selectionQuery = "select " + Constants.COLUMN_DOB_ID + ", "
                + Constants.COLUMN_DOB_NAME + ", "
                + Constants.COLUMN_DOB_DATE + " from "
                + Constants.TABLE_DATE_OF_BIRTH + " where "
                + Constants.COLUMN_DOB_NAME + " = '"
                + dob.getName() + "' COLLATE NOCASE AND "
                + Constants.COLUMN_DOB_DATE + " = '"
                + sampleDate + "'";

        System.out.println("query -- is unique --- " + selectionQuery);
        SQLiteDatabase db = DBHelper.getInstace().getReadableDatabase();
        Cursor cursor = db.rawQuery(selectionQuery, null);
        List<DateOfBirth> dobList = getDateOfBirthsFromCursor(cursor);
        cursor.close();
        db.close();
        if(dobList != null && dobList.size() > 0) {
            return true;
        }
        return false;
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
        java.sql.Date today = new java.sql.Date(new Date().getTime());

        selectionQuery = "select " + Constants.COLUMN_DOB_ID + ", "
                + Constants.COLUMN_DOB_NAME + ", "
                + Constants.COLUMN_DOB_DATE + " from "
                + Constants.TABLE_DATE_OF_BIRTH + " ORDER BY CAST (strftime('%j', "
                + Constants.COLUMN_DOB_DATE + ") AS INTEGER)";

        selectionQuery = "select " + Constants.COLUMN_DOB_ID + ", "
                + Constants.COLUMN_DOB_NAME + ", "
                + Constants.COLUMN_DOB_DATE + ", "
                + "case when day < cast(strftime('%m%d','"
                + today
                + "') as int) then priority + 1 else priority end cp from"
                + " (select "
                + Constants.COLUMN_DOB_ID + ", "
                + Constants.COLUMN_DOB_NAME + ", "
                + Constants.COLUMN_DOB_DATE + ", "
                + "cast(strftime('%m%d', "
                + Constants.COLUMN_DOB_DATE + ") as int) as day, 0 as priority from "
                + Constants.TABLE_DATE_OF_BIRTH + ") order by cp, day";


        System.out.println("query--select all --- " + selectionQuery);
        SQLiteDatabase db = DBHelper.getInstace().getReadableDatabase();
        Cursor cursor = db.rawQuery(selectionQuery, null);
        List<DateOfBirth> dobList = getDateOfBirthsFromCursor(cursor);
        cursor.close();
        db.close();
        return dobList;
    }

    public static List selectTodayAndBelated() {
        String selectionQuery;
        java.sql.Date today = new java.sql.Date(new Date().getTime());
        Calendar cal = Calendar.getInstance();
        int currentDayOfYear = Integer.parseInt(Util.getStringFromDate(new Date(), Constants.DAY_OF_YEAR));
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -Constants.RECENT_DURATION);
        int belatedDate = Integer.parseInt(Util.getStringFromDate(cal.getTime(), Constants.DAY_OF_YEAR));
        if(belatedDate > currentDayOfYear) {
            selectionQuery = "select " + Constants.COLUMN_DOB_ID + ", "
                    + Constants.COLUMN_DOB_NAME + ", "
                    + Constants.COLUMN_DOB_DATE + ", "
                    + "0 as TYPE, "
                    + "cast(strftime('%m%d', "
                    + Constants.COLUMN_DOB_DATE + ") as int) as day from "
                    + Constants.TABLE_DATE_OF_BIRTH + " where "
                    + "day <= cast(strftime('%m%d', '"
                    + today
                    + "') as int) "
                    + "UNION "
                    + "select " + Constants.COLUMN_DOB_ID + ", "
                    + Constants.COLUMN_DOB_NAME + ", "
                    + Constants.COLUMN_DOB_DATE + ", "
                    + "1 as TYPE, "
                    + "cast(strftime('%m%d', "
                    + Constants.COLUMN_DOB_DATE + ") as int) as day from "
                    + Constants.TABLE_DATE_OF_BIRTH + " where day >= cast(strftime('%m%d', date('"
                    + today
                    + "', '"
                    + (-Constants.RECENT_DURATION)
                    +" day')) as int) order by TYPE, day desc";
        }
        else {
            selectionQuery = "select " + Constants.COLUMN_DOB_ID + ", "
                    + Constants.COLUMN_DOB_NAME + ", "
                    + Constants.COLUMN_DOB_DATE + ", "
                    + "cast(strftime('%m%d', "
                    + Constants.COLUMN_DOB_DATE + ") as int) as day from "
                    + Constants.TABLE_DATE_OF_BIRTH + " where day >= (cast(strftime('%m%d', '"
                    + today
                    + "') as int) - "
                    + Constants.RECENT_DURATION + ") AND day <= cast(strftime('%m%d', '"
                    + today
                    + "') as int) order by day desc";
        }

        System.out.println("query today and belated --" + selectionQuery);
        SQLiteDatabase db = DBHelper.getInstace().getReadableDatabase();
        Cursor cursor = db.rawQuery(selectionQuery, null);
        List<DateOfBirth> dobList = getDateOfBirthsFromCursor(cursor, "Completed");
        cursor.close();
        db.close();
        return dobList;
    }

    public static List selectToday(Context context) {
        String selectionQuery;
        java.sql.Date today = new java.sql.Date(new Date().getTime());
        selectionQuery = "select " + Constants.COLUMN_DOB_ID + ", "
                + Constants.COLUMN_DOB_NAME + ", "
                + Constants.COLUMN_DOB_DATE + ", "
                + "cast(strftime('%m%d', "
                + Constants.COLUMN_DOB_DATE + ") as int) as day from "
                + Constants.TABLE_DATE_OF_BIRTH + " where day = (cast(strftime('%m%d', '"
                + today
                + "') as int) "
                + ") order by day desc";


        System.out.println("query today -- " + selectionQuery);
        SQLiteDatabase db = DBHelper.createInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectionQuery, null);
        List<DateOfBirth> dobList = getDateOfBirthsFromCursor(cursor, "Completed");
        cursor.close();
        db.close();
        return dobList;
    }

    public static int getCountTodayAndBelated() {
        String selectionQuery;
        java.sql.Date today = new java.sql.Date(new Date().getTime());
        selectionQuery = "select count(" + Constants.COLUMN_DOB_ID + "), "
                + "cast(strftime('%m%d', "
                + Constants.COLUMN_DOB_DATE + ") as int) as day from "
                + Constants.TABLE_DATE_OF_BIRTH + " where day = cast(strftime('%m%d', '"
                + today
                + "') as int)";

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

    public static List<DateOfBirth> getDateOfBirthsFromCursor(Cursor cursor, String description) {
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

    public static List<DateOfBirth> getDateOfBirthsFromCursor(Cursor cursor) {
        return getDateOfBirthsFromCursor(cursor, "Completing");
    }

    public static String deleteAll() {
        SQLiteDatabase db = DBHelper.getInstace().getWritableDatabase();

//		String selectionQuery;
//		selectionQuery = String.format("delete from %s", TABLE_DOB);
//		System.out.println("query--" + selectionQuery);
//		db.rawQuery(selectionQuery, null);
        int returnValue = db.delete(Constants.TABLE_DATE_OF_BIRTH, null, null);
        db.close();
        Log.i("delete all", returnValue + "");
        return Constants.NOTIFICATION_DELETE_1001;
    }

    public Boolean deleteRecordForTheId(int givenId) {
        SQLiteDatabase db = DBHelper.getInstace().getWritableDatabase();
        return db.delete(Constants.TABLE_DATE_OF_BIRTH, Constants.COLUMN_DOB_ID + "=" + givenId, null) > 0;
    }
}

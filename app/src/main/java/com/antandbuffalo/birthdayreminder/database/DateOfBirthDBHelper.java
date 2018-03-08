package com.antandbuffalo.birthdayreminder.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DataHolder;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

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

    //returns true is the given values is not available in DB
    //returns false if the entry is available already
    public static boolean isUniqueDateOfBirth(DateOfBirth dob) {
        SQLiteDatabase db = DBHelper.getInstace().getReadableDatabase();

        String[] columns = {Constants.COLUMN_DOB_ID, Constants.COLUMN_DOB_NAME, Constants.COLUMN_DOB_DATE, Constants.COLUMN_DOB_OPTIONAL_YEAR};
        String selection = Constants.COLUMN_DOB_NAME + " =? AND " + Constants.COLUMN_DOB_DATE + " =? AND " + Constants.COLUMN_DOB_OPTIONAL_YEAR + " =?";
        String optionalYear = dob.getRemoveYear()? "1" : "0";
        String[] selectionArgs = {dob.getName(), Util.getStringFromDate(dob.getDobDate()), optionalYear};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;
        Cursor cursor = db.query(Constants.TABLE_DATE_OF_BIRTH, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

        //Cursor cursor = db.rawQuery(selectionQuery, null);
        List<DateOfBirth> dobList = getDateOfBirthsFromCursor(cursor);
        cursor.close();
        db.close();
        if(dobList != null && dobList.size() > 0) {
            return false;
        }
        return true;
    }

    public static boolean isUniqueDateOfBirthIgnoreCase(DateOfBirth dob) {
        SQLiteDatabase db = DBHelper.getInstace().getReadableDatabase();

        String[] columns = {Constants.COLUMN_DOB_ID, Constants.COLUMN_DOB_NAME, Constants.COLUMN_DOB_DATE, Constants.COLUMN_DOB_OPTIONAL_YEAR};

        String optionalYear = dob.getRemoveYear()? "1" : "0";

        String selection = Constants.COLUMN_DOB_NAME + " =? COLLATE NOCASE AND " + Constants.COLUMN_DOB_DATE + " =? AND " + Constants.COLUMN_DOB_OPTIONAL_YEAR + " =?";
        if(optionalYear.equalsIgnoreCase("0")) {
            selection = Constants.COLUMN_DOB_NAME + " =? COLLATE NOCASE AND " + Constants.COLUMN_DOB_DATE + " =? AND (" + Constants.COLUMN_DOB_OPTIONAL_YEAR + " =? OR " + Constants.COLUMN_DOB_OPTIONAL_YEAR + " is NULL)";
        }

        String[] selectionArgs = {dob.getName(), Util.getStringFromDate(dob.getDobDate()), optionalYear};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;
        Cursor cursor = db.query(Constants.TABLE_DATE_OF_BIRTH, columns, selection, selectionArgs, groupBy, having, orderBy, limit);

        //Cursor cursor = db.rawQuery(selectionQuery, null);
        List<DateOfBirth> dobList = getDateOfBirthsFromCursor(cursor);
        cursor.close();
        db.close();
        if(dobList != null && dobList.size() > 0) {
            return false;
        }
        return true;
    }

    public static long insertDOB(DateOfBirth dateOfBirth) {
        DBHelper dbHelper = DBHelper.getInstace();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_DOB_NAME, dateOfBirth.getName()); // Contact Name
        values.put(Constants.COLUMN_DOB_DATE, Util.getStringFromDate(dateOfBirth.getDobDate())); // date of birth - 2000
        values.put(Constants.COLUMN_DOB_OPTIONAL_YEAR, dateOfBirth.getRemoveYear());
        long returnValue = db.insert(Constants.TABLE_DATE_OF_BIRTH, null, values); // Inserting Row
        db.close();
        return returnValue;
    }

    public static long updateDOB(DateOfBirth dateOfBirth) {
        DBHelper dbHelper = DBHelper.getInstace();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String updateQuery = "UPDATE " + Constants.TABLE_DATE_OF_BIRTH + " SET "
                + Constants.COLUMN_DOB_NAME + " = " + dateOfBirth.getName() + ", "
                + Constants.COLUMN_DOB_DATE + " = " + Util.getStringFromDate(dateOfBirth.getDobDate())
                + " WHERE "
                + Constants.COLUMN_DOB_ID + " = " + dateOfBirth.getDobId();

        System.out.println("query -- update dob --- " + updateQuery);
        //Cursor cursor = db.rawQuery(updateQuery, null);

        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_DOB_NAME, dateOfBirth.getName()); // Contact Name
        values.put(Constants.COLUMN_DOB_DATE, Util.getStringFromDate(dateOfBirth.getDobDate())); // date of birth - 2000
        values.put(Constants.COLUMN_DOB_OPTIONAL_YEAR, dateOfBirth.getRemoveYear());
        // update Row
        String dobId = Constants.COLUMN_DOB_ID + "=" + dateOfBirth.getDobId();
        long returnValue = db.update(Constants.TABLE_DATE_OF_BIRTH, values, dobId, null);
        Log.i("after update", returnValue + "");
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
                + Constants.COLUMN_DOB_OPTIONAL_YEAR + ", "
                + "case when day < cast(strftime('%m%d','"
                + today
                + "') as int) then priority + 1 else priority end cp from"
                + " (select "
                + Constants.COLUMN_DOB_ID + ", "
                + Constants.COLUMN_DOB_NAME + ", "
                + Constants.COLUMN_DOB_DATE + ", "
                + Constants.COLUMN_DOB_OPTIONAL_YEAR + ", "
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

    public static List selectUpcoming() {
        List<DateOfBirth> dobList = selectAll();
        int currentDayInNumber = Integer.parseInt(Util.getStringFromDate(new Date(), Constants.DAY_OF_YEAR));
        int birthdayInNumber = 0;
        for(DateOfBirth dateOfBirth : dobList) {
            birthdayInNumber = Integer.parseInt(Util.getStringFromDate(dateOfBirth.getDobDate(), Constants.DAY_OF_YEAR));
            if(currentDayInNumber == birthdayInNumber) {
                Util.setDescription(dateOfBirth, "Completed");
            }
            else {
                dateOfBirth.setAge(dateOfBirth.getAge() + 1);
                Util.setDescription(dateOfBirth, "Completing");
            }
        }

        return dobList;
    }

    public static List selectTodayAndBelated() {
        String selectionQuery;
        SharedPreferences sharedPreferences = Util.getSharedPreference();
        int recentDuration = sharedPreferences.getInt(Constants.PREFERENCE_RECENT_DAYS_TODAY, 0);

        Calendar cal = Calendar.getInstance();
        int currentDayOfYear = Integer.parseInt(Util.getStringFromDate(new Date(), Constants.DAY_OF_YEAR));
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -recentDuration);
        int belatedDate = Integer.parseInt(Util.getStringFromDate(cal.getTime(), Constants.DAY_OF_YEAR));

        java.sql.Date today = new java.sql.Date(new Date().getTime());
        java.sql.Date todayRecent = new java.sql.Date(cal.getTimeInMillis());

        if(belatedDate > currentDayOfYear) {
            selectionQuery = "select " + Constants.COLUMN_DOB_ID + ", "
                    + Constants.COLUMN_DOB_NAME + ", "
                    + Constants.COLUMN_DOB_DATE + ", "
                    + Constants.COLUMN_DOB_OPTIONAL_YEAR + ", "
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
                    + (-recentDuration)
                    +" day')) as int) order by TYPE, day desc";
        }
        else {
            selectionQuery = "select " + Constants.COLUMN_DOB_ID + ", "
                    + Constants.COLUMN_DOB_NAME + ", "
                    + Constants.COLUMN_DOB_DATE + ", "
                    + Constants.COLUMN_DOB_OPTIONAL_YEAR + ", "
                    + "cast(strftime('%m%d', "
                    + Constants.COLUMN_DOB_DATE + ") as int) as day from "
                    + Constants.TABLE_DATE_OF_BIRTH + " where day >= cast(strftime('%m%d', '"
                    + todayRecent
                    + "') as int)"
                    + " AND day <= cast(strftime('%m%d', '"
                    + today
                    + "') as int) order by day desc";
        }

        System.out.println("query today and belated --" + selectionQuery);
        SQLiteDatabase db = DBHelper.getInstace().getReadableDatabase();
        Cursor cursor = db.rawQuery(selectionQuery, null);
        List<DateOfBirth> dobList = getDateOfBirthsFromCursor(cursor);
        for(DateOfBirth dateOfBirth : dobList) {
            Util.setDescription(dateOfBirth, "Completed");
        }
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
        List<DateOfBirth> dobList = getDateOfBirthsFromCursor(cursor);
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

    //This function purely creates the objects list and return
    public static List<DateOfBirth> getDateOfBirthsFromCursor(Cursor cursor) {
        List<DateOfBirth> dobList = new ArrayList<DateOfBirth>();
        if (cursor.moveToFirst()) {
            do {
                DateOfBirth dateOfBirth = new DateOfBirth();
                dateOfBirth.setDobId(cursor.getLong(0));
                dateOfBirth.setName(cursor.getString(1));
                dateOfBirth.setDobDate(Util.getDateFromString(cursor.getString(2)));
                dateOfBirth.setAge(Util.getAge(dateOfBirth.getDobDate()));
                if(cursor.getInt(3) == 1) {
                    dateOfBirth.setRemoveYear(true);
                }
                else {
                    dateOfBirth.setRemoveYear(false);
                }
                dobList.add(dateOfBirth);
            } while (cursor.moveToNext());
        }
        return dobList;
    }

    public static String deleteAll() {
        int numberOfRowsDeleted = DBHelper.deleteAll(Constants.TABLE_DATE_OF_BIRTH);
        return Constants.NOTIFICATION_DELETE_1001;
    }

    public static Boolean deleteRecordForTheId(long givenId) {
        SQLiteDatabase db = DBHelper.getInstace().getWritableDatabase();
        return db.delete(Constants.TABLE_DATE_OF_BIRTH, Constants.COLUMN_DOB_ID + "=" + givenId, null) > 0;
    }
}

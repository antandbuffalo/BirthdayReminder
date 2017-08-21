package com.antandbuffalo.birthdayreminder.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.settings.Settings;
import com.antandbuffalo.birthdayreminder.settings.SettingsModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by i677567 on 12/10/15.
 */
public class OptionsDBHelper {

    public static void insertDefaultValues() {
        List<SettingsModel> data = new ArrayList();
        SettingsModel datum;
        JSONObject extraFields;

//        datum = SettingsModel.newInstance();
//        datum.setKey(Constants.TYPE_ADD_NEW);
//        datum.setType("NORMAL");
//        datum.setTitle("Add New");
//        data.add(datum);

        datum = SettingsModel.newInstance();
        datum.setKey(Constants.SETTINGS_WRITE_FILE);
        datum.setTitle(Constants.SETTINGS_WRITE_FILE_TITLE);
        datum.setSubTitle(Constants.SETTINGS_WRITE_FILE_SUB_TITLE);
        //datum.setUpdatedOn(new Date());
        data.add(datum);

        datum = SettingsModel.newInstance();
        datum.setKey(Constants.SETTINGS_READ_FILE);
        datum.setTitle(Constants.SETTINGS_READ_FILE_TITLE);
        datum.setSubTitle(Constants.SETTINGS_READ_FILE_SUB_TITLE);
        //datum.setUpdatedOn(new Date());
        data.add(datum);

        datum = SettingsModel.newInstance();
        datum.setKey(Constants.SETTINGS_DELETE_ALL);
        datum.setTitle(Constants.SETTINGS_DELETE_ALL_TITLE);
        datum.setSubTitle("");
        //datum.setUpdatedOn(new Date());
        data.add(datum);

        datum = SettingsModel.newInstance();
        datum.setKey(Constants.SETTINGS_ABOUT);
        datum.setTitle("About");
        datum.setSubTitle("");
        //datum.setUpdatedOn(new Date());
        extraFields = new JSONObject();
        Util.validateAndSetExtra(extraFields, "iconLetter", "A");
        datum.setExtra(extraFields.toString());
        data.add(datum);


        for(SettingsModel option : data) {
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_OPTION_CODE, option.getKey());
            values.put(Constants.COLUMN_OPTION_TITLE, option.getTitle());
            values.put(Constants.COLUMN_OPTION_SUBTITLE, option.getSubTitle());
            values.put(Constants.COLUMN_OPTION_UPDATED_ON, Util.getStringFromDate(option.getUpdatedOn()));
            values.put(Constants.COLUMN_OPTION_EXTRA, option.getExtra());
            DBHelper.getInstace().getWritableDatabase().insert(Constants.TABLE_OPTIONS, null, values); // Inserting Row
        }
    }

    public static long addOptions(SettingsModel option) {
        return insertOption(option);
    }

    public static long insertOption(SettingsModel option) {
        DBHelper dbHelper = DBHelper.getInstace();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_OPTION_CODE, option.getKey());
        values.put(Constants.COLUMN_OPTION_TITLE, option.getTitle());
        values.put(Constants.COLUMN_OPTION_SUBTITLE, option.getSubTitle());
        values.put(Constants.COLUMN_OPTION_UPDATED_ON, Util.getStringFromDate(option.getUpdatedOn()));
        values.put(Constants.COLUMN_OPTION_EXTRA, option.getExtra());
        long returnValue = db.insert(Constants.TABLE_OPTIONS, null, values); // Inserting Row
        db.close();
        return returnValue;
    }

    public static long updateOption(SettingsModel option) {
        DBHelper dbHelper = DBHelper.getInstace();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_OPTION_SUBTITLE, option.getSubTitle());
        values.put(Constants.COLUMN_OPTION_UPDATED_ON, Util.getStringFromDate(option.getUpdatedOn()));
        String where = Constants.COLUMN_OPTION_CODE + " = ?";
        long returnValue = db.update(Constants.TABLE_OPTIONS, values, where, new String[]{option.getKey()});
        db.close();
        return returnValue;
    }

    public static List selectAll() {
        // Select All Query
        String selectionQuery;

        selectionQuery = "select "
                + Constants.COLUMN_OPTION_CODE + ", "
                + Constants.COLUMN_OPTION_TITLE + ", "
                + Constants.COLUMN_OPTION_SUBTITLE + ", "
                + Constants.COLUMN_OPTION_UPDATED_ON + ", "
                + Constants.COLUMN_OPTION_EXTRA
                + " from "
                + Constants.TABLE_OPTIONS;

        System.out.println("query-- select all options --- " + selectionQuery);
        SQLiteDatabase db = DBHelper.getInstace().getReadableDatabase();
        Cursor cursor = db.rawQuery(selectionQuery, null);
        List<SettingsModel> options = getOptionsFromCursor(cursor);
        cursor.close();
        db.close();
        return options;
    }

    public static List<SettingsModel> getOptionsFromCursor(Cursor cursor) {
        List<SettingsModel> options = new ArrayList<SettingsModel>();
        if (cursor.moveToFirst()) {
            do {
                SettingsModel settingsModel = new SettingsModel();
                settingsModel.setKey(cursor.getString(0));
                settingsModel.setTitle(cursor.getString(1));
                settingsModel.setSubTitle(cursor.getString(2));
                settingsModel.setUpdatedOn(Util.getDateFromString(cursor.getString(3)));
                settingsModel.setExtra(cursor.getString(4));
                settingsModel.setExtraJson(Util.parseJSON(settingsModel.getExtra()));

                // Adding contact to list
                options.add(settingsModel);
            } while (cursor.moveToNext());
        }
        return options;
    }
    public static String deleteAll() {
        int numberOfRowsDeleted = DBHelper.deleteAll(Constants.TABLE_OPTIONS);
        return Constants.NOTIFICATION_DELETE_1001;
    }
    public static long getNumberOfRows() {
        long numberOfRows = DBHelper.getNumberOfRows(Constants.TABLE_OPTIONS);
        return numberOfRows;
    }
}

package com.antandbuffalo.birthdayreminder.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.settings.Settings;
import com.antandbuffalo.birthdayreminder.settings.SettingsModel;
import com.antandbuffalo.birthdayreminder.settings.SettingsViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by i677567 on 12/10/15.
 */
public class OptionsDBHelper {

    public static List getDefatultValues() {
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
        datum.setSno(1);
        data.add(datum);

        datum = SettingsModel.newInstance();
        datum.setKey(Constants.SETTINGS_READ_FILE);
        datum.setTitle(Constants.SETTINGS_READ_FILE_TITLE);
        datum.setSubTitle(Constants.SETTINGS_READ_FILE_SUB_TITLE);
        //datum.setUpdatedOn(new Date());
        datum.setSno(2);
        data.add(datum);

        datum = SettingsModel.newInstance();
        datum.setKey(Constants.SETTINGS_DELETE_ALL);
        datum.setTitle(Constants.SETTINGS_DELETE_ALL_TITLE);
        datum.setSubTitle("");
        //datum.setUpdatedOn(new Date());
        datum.setSno(3);
        data.add(datum);

        datum = SettingsModel.newInstance();
        datum.setKey(Constants.SETTINGS_MODIFY_TODAY);
        datum.setTitle("Modify Today Section");
        datum.setSubTitle("");
        extraFields = new JSONObject();
        Util.validateAndSetExtra(extraFields, Constants.SETTINGS_ICON_LETTER, "T");
        datum.setExtra(extraFields.toString());
        datum.setSno(4);
        data.add(datum);

        datum = SettingsModel.newInstance();
        datum.setKey(Constants.SETTINGS_NOTIFICATION);
        datum.setTitle("Notification Settings");
        datum.setSubTitle("");
        extraFields = new JSONObject();
        Util.validateAndSetExtra(extraFields, Constants.SETTINGS_ICON_LETTER, "N");
        datum.setExtra(extraFields.toString());
        datum.setSno(5);
        data.add(datum);

        datum = SettingsModel.newInstance();
        datum.setKey(Constants.SETTINGS_WISH_TEMPLATE);
        datum.setTitle("Wish Template");
        datum.setSubTitle("");
        extraFields = new JSONObject();
        Util.validateAndSetExtra(extraFields, Constants.SETTINGS_ICON_LETTER, "W");
        datum.setExtra(extraFields.toString());
        datum.setSno(6);
        data.add(datum);

        datum = SettingsModel.newInstance();
        datum.setKey(Constants.SETTINGS_ABOUT);
        datum.setTitle("About");
        datum.setSubTitle("");
        extraFields = new JSONObject();
        Util.validateAndSetExtra(extraFields, Constants.SETTINGS_ICON_LETTER, "A");
        datum.setExtra(extraFields.toString());
        datum.setSno(7);
        data.add(datum);
        return data;
    }
    public static void insertDefaultValues() {
        List<SettingsModel> data = getDefatultValues();
        for(SettingsModel option : data) {

            long status = insertOption(option);
            Log.i("Option insertition", status + "");
        }
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
        values.put(Constants.COLUMN_OPTION_SNO, option.getSno());
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
                + Constants.COLUMN_OPTION_EXTRA + ", "
                + Constants.COLUMN_OPTION_SNO
                + " from "
                + Constants.TABLE_OPTIONS + " ORDER BY " + Constants.COLUMN_OPTION_SNO + " ASC";

        System.out.println("query-- select all options --- " + selectionQuery);
        SQLiteDatabase db = DBHelper.getInstace().getReadableDatabase();
        Cursor cursor = db.rawQuery(selectionQuery, null);
        List<SettingsModel> options = getOptionsFromCursor(cursor);
        Collections.sort(options, (settingsModel, t1) -> Integer.compare(settingsModel.getSno(), t1.getSno()));
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
                settingsModel.setSno(cursor.getInt(5));
                //settingsModel.setExtraJson(Util.parseJSON(settingsModel.getExtra()));

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
    public static String getExtraValue(SettingsModel settingsModel, String key) {
        String returnValue = null;
        if(settingsModel.getExtra() == null) {
            returnValue = null;
        }
        else if(settingsModel.getExtraJson() == null) {
            try {
                settingsModel.setExtraJson(new JSONObject(settingsModel.getExtra()));
                Log.i("summa ", (String)settingsModel.getExtraJson().get(key));
                returnValue = (String)settingsModel.getExtraJson().get(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                returnValue = (String)settingsModel.getExtraJson().get(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Log.i("before return ", returnValue);
        return returnValue;
    }
    public static void initSNO(SQLiteDatabase db) {
        List<SettingsModel> data = getDefatultValues();
        for(SettingsModel option : data) {
            ContentValues values = new ContentValues();
            values.put(Constants.COLUMN_OPTION_SNO, Constants.OPIONS_SNO_MAPPER.get(option.getKey()));
            String where = Constants.COLUMN_OPTION_CODE + " = ?";
            db.update(Constants.TABLE_OPTIONS, values, where, new String[]{option.getKey()});
        }
    }

    public static long updateSNO(SettingsModel newOption) {
        DBHelper dbHelper = DBHelper.getInstace();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_OPTION_SNO, newOption.getSno()); //update sno to new one
        // update Row
        String optionCode = Constants.COLUMN_OPTION_CODE + "='" + newOption.getKey() + "'";
        long returnValue = db.update(Constants.TABLE_OPTIONS, values, optionCode, null);

        Log.i("after update", returnValue + "");
        db.close();
        return returnValue;
    }

    public static void populatePage() {
        List<SettingsModel> defaultData = getDefatultValues();
        long totalRows = OptionsDBHelper.getNumberOfRows();
        if(totalRows == 0) {
            OptionsDBHelper.insertDefaultValues();
        }
        else {
            if(totalRows != Constants.OPTIONS_TABLE_NUMBER_OF_ROWS) {
                List<SettingsModel> optionsDbData = selectAll();
                for(SettingsModel defaultOption : defaultData) {
                    boolean isAlreadyInDB = false;
                    boolean needToUpdateSno = false;
                    for(SettingsModel currentOption : optionsDbData) {
                        if(defaultOption.getKey().equalsIgnoreCase(currentOption.getKey())) {
                            isAlreadyInDB = true;
                            if(defaultOption.getSno() != currentOption.getSno()) {
                                needToUpdateSno = true;
                            }
                        }
                    }
                    if(!isAlreadyInDB) {
                        insertOption(defaultOption);
                    }
                    if(needToUpdateSno) {
                        updateSNO(defaultOption);
                    }
                }
            }
        }
    }
}

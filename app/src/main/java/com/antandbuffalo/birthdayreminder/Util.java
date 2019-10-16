package com.antandbuffalo.birthdayreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.antandbuffalo.birthdayreminder.common.Storage;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;
import com.antandbuffalo.birthdayreminder.database.OptionsDBHelper;
import com.antandbuffalo.birthdayreminder.notification.AlarmReceiver;
import com.antandbuffalo.birthdayreminder.settings.SettingsModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by i677567 on 5/10/15.
 */
public class Util {

    public static int compareDateAndMonth(Date date1, Date date2) {
        Calendar calendar1 = Util.getCalendar(date1);
        Calendar calendar2 = Util.getCalendar(date2);
        if(calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) && calendar1.get(Calendar.DATE) == calendar2.get(Calendar.DATE)) {
            return 0;
        }
        else {
            return 1;
        }
    }

    public static String getTwoDigitsString(int number) {
        if(number < 10) {
            return "0" + number;
        }
        return number + "";
    }

    public static long getDaysBetweenDates(Date date1, Date date2) {
        //http://stackoverflow.com/questions/3838527/android-java-date-difference-in-days
        Calendar calDate1 = Calendar.getInstance();
        calDate1.setTime(date1);
        Calendar calDate2 = Calendar.getInstance();
        calDate2.setTime(date2);

        long diff = calDate2.getTimeInMillis() - calDate1.getTimeInMillis();
        long days = diff / (24 * 60 * 60 * 1000);
        return days;
    }

    public static long getDaysBetweenDates(Date date1) {
        return getDaysBetweenDates(date1, new Date());
    }

    public static int convertDPtoPixel(float density, Resources resources) {
        // Get the screen's density scale
        final float scale = resources.getDisplayMetrics().density;
    // Convert the dps to pixels, based on density scale
        return (int) (density * scale + 0.5f);
    }

    public static Date getDateFromString(String input) {
        if(input == null) {
            return null;
        }
        DateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getStringFromDate(Date date) {
        return getStringFromDate(date, Constants.DATE_FORMAT);
    }

    public static String getStringFromDate(Date date, String dateFormat) {
        if(date == null || dateFormat == null) {
            return null;
        }
        SimpleDateFormat dateFormater = new SimpleDateFormat(dateFormat, Locale.getDefault());
        return dateFormater.format(date);
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static Calendar getCalendar() {
        return Util.getCalendar(new Date());
    }

    public static int getAge(Date date) {
        Calendar birthDate = getCalendar(date);
        Calendar currentDate = getCalendar(new Date());
        int age = currentDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        int currentDayInNumber = Integer.parseInt(Util.getStringFromDate(currentDate.getTime(), Constants.DAY_OF_YEAR));
        int birthDayInNumber = Integer.parseInt(Util.getStringFromDate(birthDate.getTime(), Constants.DAY_OF_YEAR));

        if(currentDayInNumber < birthDayInNumber) {
            age--;
        }
        return age;
    }

    public static String readFromFile(String fileName) {
        String returnValue = "";
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // sdcard not found. read from bundle
            //SD Card not found.
            return Constants.ERROR_NO_SD_CARD;
        }
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            // Get the text file
            File file = new File(sdcard, Constants.FOLDER_NAME + "/" + fileName + Constants.FILE_NAME_SUFFIX);
            if (!file.exists()) {
                Toast.makeText(DataHolder.getInstance().getAppContext(), Constants.ERROR_NO_BACKUP_FILE, Toast.LENGTH_SHORT).show();
                return Constants.ERROR_NO_BACKUP_FILE;
            }
            BufferedReader br = new BufferedReader(new BufferedReader(new FileReader(file)));
            String strLine, n1, d1, m1, y1;
            String regexStr = "^[0-9]+$";
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                DateOfBirth dob = new DateOfBirth();
                dob.setRemoveYear(false);
                String[] lineComponents = strLine.split(" ");

                n1 = lineComponents[0];
                n1 = n1.replace("_", " ");
                d1 = lineComponents[1];
                m1 = lineComponents[2];
                y1 = lineComponents[3];

                if(lineComponents.length == 5) {
                     if(lineComponents[4].equals("1")) {
                         dob.setRemoveYear(true);
                     }
                }

                if(!d1.trim().matches(regexStr) || !m1.trim().matches(regexStr) || !y1.trim().matches(regexStr)) {
                    //write code here for failure
                    continue;
                }

                String dateStr = lineComponents[1] + " " + lineComponents[2] + " " + lineComponents[3];
                dob.setName(n1);
                DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_WITH_SPACE);
                dob.setDobDate(dateFormat.parse(dateStr));
                if (DateOfBirthDBHelper.isUniqueDateOfBirthIgnoreCase(dob)) {
                    DateOfBirthDBHelper.insertDOB(dob);
                }
            }
            //Close the input stream
            br.close();
            returnValue = Constants.NOTIFICATION_SUCCESS_DATA_LOAD;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            returnValue = Constants.ERROR_UNKNOWN;
        }
        System.out.println("Read successful");
        return returnValue;
    }

    public static String writeToFile() {
        String returnValue = "";
        String createFolderResult = Util.createEmptyFolder();
        if(!createFolderResult.equalsIgnoreCase(Constants.FLAG_SUCCESS)) {
            return createFolderResult;
        }
        File sdcard = Environment.getExternalStorageDirectory();
        List<DateOfBirth> dobs = DateOfBirthDBHelper.selectAll();
        if(dobs == null || dobs.size() == 0) {
            //Toast.makeText(DataHolder.getInstance().getAppContext(), Constants.ERROR_READ_WRITE_1005, Toast.LENGTH_LONG).show();
            return Constants.MSG_NOTHING_TO_BACKUP_DATA_EMPTY;
        }
        Calendar calendar = getCalendar(new Date());
        String currentDateTime = calendar.get(Calendar.YEAR) + getTwoDigitsString(calendar.get(Calendar.MONTH) + 1) + getTwoDigitsString(calendar.get(Calendar.DATE))
                + getTwoDigitsString(calendar.get(Calendar.HOUR_OF_DAY)) + getTwoDigitsString(calendar.get(Calendar.MINUTE)) + getTwoDigitsString(calendar.get(Calendar.SECOND));
        String fileName = "/" + Constants.FOLDER_NAME + "/" + Constants.FILE_NAME + Constants.FILE_NAME_SUFFIX;
        String fileNameBackup = "/" + Constants.FOLDER_NAME + "/" + Constants.FILE_NAME + "_" + currentDateTime + Constants.FILE_NAME_SUFFIX;

        File myFile = new File(sdcard, fileName);
        File myFileBackup = new File(sdcard, fileNameBackup);
        myFile.renameTo(myFileBackup);

        try {
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            for (DateOfBirth dob : dobs) {
                String name = dob.getName().replace(" ", Constants.SPACE_REPLACER);

                Calendar cal = getCalendar(dob.getDobDate());
                String dobString = cal.get(Calendar.DATE) + " "
                        + (cal.get(Calendar.MONTH) + 1) + " "
                        + cal.get(Calendar.YEAR);
                String isRemoveYear = dob.getRemoveYear()? "1" : "0";
                myOutWriter.append(name + " " + dobString + " " + isRemoveYear);
                myOutWriter.append("\n");
            }
            myOutWriter.close();
            fOut.close();
            System.out.println("Write successful");
            returnValue = Constants.NOTIFICATION_READ_WRITE_1001;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            //Toast.makeText(DataHolder.getInstance().getAppContext(), Constants.ERROR_READ_WRITE_1003, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            returnValue = Constants.ERROR_UNKNOWN;
        }
        return returnValue;
    }

    public static String updateFile(DateOfBirth dob) {
        String returnValue = "";
        String createFolderStatus = Util.createEmptyFolder();
        if(!createFolderStatus.equalsIgnoreCase(Constants.FLAG_SUCCESS)) {
            return createFolderStatus;
        }
        File sdcard = Environment.getExternalStorageDirectory();
        long currentMillis = System.currentTimeMillis();
        String fileName = "/" + Constants.FOLDER_NAME + "/" + Constants.FILE_NAME + Constants.FILE_NAME_SUFFIX;

        File myFile = new File(sdcard, fileName);

        try {
            FileOutputStream fOut = new FileOutputStream(myFile, true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

            String name = dob.getName().replace(" ", Constants.SPACE_REPLACER);

            Calendar cal = getCalendar(dob.getDobDate());

            String dobString = cal.get(Calendar.DATE) + " "
                    + (cal.get(Calendar.MONTH) + 1) + " "
                    + cal.get(Calendar.YEAR);
            String isRemoveYear = dob.getRemoveYear()? "1" : "0";
            myOutWriter.append(name + " " + dobString + " " + isRemoveYear);
            myOutWriter.append("\n");

            myOutWriter.close();
            fOut.close();
            System.out.println("Write successful");
            returnValue = Constants.STATUS_FILE_APPEND_SUCCESS;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            //Toast.makeText(DataHolder.getInstance().getAppContext(), Constants.ERROR_READ_WRITE_1003, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            returnValue = Constants.ERROR_UNKNOWN;
        }
        return returnValue;
    }

    public static String readFromAssetFile(String defaultFileName) {
        try {
            BufferedReader br;
            DataInputStream in = null;

            AssetManager am = DataHolder.getInstance().getAppContext().getAssets();
            InputStream fstream = am.open(defaultFileName);
            // Get the object of DataInputStream
            in = new DataInputStream(fstream);

            br = new BufferedReader(new InputStreamReader(in));

            String strLine, n1, d1, m1, y1;
            DateOfBirth dateOfBirth = new DateOfBirth();
            String regexStr = "^[0-9]+$";
            // Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console afdf
                //System.out.println("in main ac -- " + strLine);
                dateOfBirth.setRemoveYear(false);

                String[] lineComponents = strLine.split(" ");
                n1 = lineComponents[0];
                n1 = n1.replace("_", " ");
                d1 = lineComponents[1];
                m1 = lineComponents[2];
                y1 = lineComponents[3];

                if(lineComponents.length == 5) {
                    if(lineComponents[4].equals("1")) {
                        dateOfBirth.setRemoveYear(true);
                    }
                }

                if(!d1.trim().matches(regexStr) || !m1.trim().matches(regexStr) || !y1.trim().matches(regexStr)) {
                    //write code here for failure
                    continue;
                }

                dateOfBirth.setName(n1);
                dateOfBirth.setDobDate(Util.getDateFromString(y1 + "-" + m1 + "-" + d1));
                if(DateOfBirthDBHelper.isUniqueDateOfBirth(dateOfBirth)) {
                    DateOfBirthDBHelper.insertDOB(dateOfBirth);
                }
//                else {
//                    Log.i("Entry already available", dateOfBirth.getName());
//                }
            }
            // Close the input stream
            //progressDialog.dismiss();
            in.close();
        }
        catch (Exception e) {// Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return "TRUE";
    }
    public static String createEmptyFolder() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //throw error sd card not found
            //Toast.makeText(DataHolder.getInstance().getAppContext(), Constants.ERROR_READ_WRITE_1004, Toast.LENGTH_LONG).show();
            return Constants.ERR_SD_CARD_NOT_FOUND;
        }
        File sdcard = Environment.getExternalStorageDirectory();
        File folder = new File(sdcard + File.separator + Constants.FOLDER_NAME);
        if(!folder.exists()) {
            folder.mkdir();
        }
        return Constants.FLAG_SUCCESS;
    }
    public static String fileToLoad(String input) {
        String key = input.toLowerCase();
        HashMap <String, String> fileNames = new HashMap<String, String>();
        fileNames.put("csea", "csea.txt");
        fileNames.put("cse", "cse.txt");
        fileNames.put("myfamily", "myfamily.txt");
        fileNames.put("thuda family", "thudaFamily.txt");
        fileNames.put("thudafamily", "thudaFamily.txt");
        fileNames.put("rengalla family", "rengallaFamily.txt");
        fileNames.put("rengallafamily", "rengallaFamily.txt");
        fileNames.put("9planets", "9planets.txt");
        fileNames.put("9 planets", "9planets.txt");
        fileNames.put("dxdx", "dxdx.txt");
        fileNames.put("inext", "inext.txt");
        if(fileNames.get(key) != null) {
            return fileNames.get(key);
        }
        return null;
    }
    public static Boolean isBackupFileFound() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //throw error sd card not found
            return false;
        }
        File sdcard = Environment.getExternalStorageDirectory();
        // Get the text file
        File file = new File(sdcard, Constants.FOLDER_NAME + "/" + Constants.FILE_NAME + Constants.FILE_NAME_SUFFIX);
        return file.exists();
    }

    public static JSONObject validateAndSetExtra(JSONObject jsonObject, String key, String value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    public static JSONObject parseJSON(String givenString) {
        JSONObject jsonObject = null;
        if(givenString != null) {
            try {
                jsonObject = new JSONObject(givenString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }
    public static SharedPreferences getSharedPreference() {
        SharedPreferences settings = DataHolder.getInstance().getAppContext().getSharedPreferences(Constants.PREFERENCE_NAME, 0);
        return settings;
    }
    public static void updateBackupTime(SettingsModel option) {
        option.setSubTitle("Last backup was");
        option.setUpdatedOn(new Date());
        OptionsDBHelper.updateOption(option);
    }
    public static void updateRestoreTime(SettingsModel option) {
        option.setSubTitle("Last restore was");
        option.setUpdatedOn(new Date());
        OptionsDBHelper.updateOption(option);
    }
    public static void setDescription(DateOfBirth dob, String info) {
        if(dob.getAge() <= 1) {
            //dob.setAge(0);
            dob.setDescription(info + ": " + dob.getAge() + " year");
        }
        else {
            dob.setDescription(info + ": " + dob.getAge() + " years");
        }
    }

    public static List<String> getMonths() {
        String[] months = new DateFormatSymbols().getShortMonths();
        List<String> monthsList = Arrays.asList(months);
        for(int i = 0; i < monthsList.size(); i++) {
            String digit = getTwoDigitsString(i + 1);
            monthsList.set(i, monthsList.get(i) + " (" + digit + ")");
        }

        return monthsList;
    }

    public static Integer getDayOfYear(Date date) {
        return Integer.parseInt(Util.getStringFromDate(date, Constants.DAY_OF_YEAR));
    }

    public static Integer getCurrentDayOfYear() {
        return Integer.parseInt(Util.getStringFromDate(new Date(), Constants.DAY_OF_YEAR));
    }

    public static Integer getRecentDayOfYear() {
        Calendar cal = Util.getCalendar();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, Constants.RECENT_DURATION);

        return Integer.parseInt(Util.getStringFromDate(cal.getTime(), Constants.DAY_OF_YEAR));
    }
    public static Boolean isCurrentYear(int year) {
        Calendar calendar = Util.getCalendar(new Date());
        return calendar.get(Calendar.YEAR) == year;
    }
    public static int getCurrentMonth() {
        Calendar calendar = Util.getCalendar(new Date());
        return calendar.get(Calendar.MONTH);
    }
    public static Boolean isCurrentMonth(int month) {
        return month == getCurrentMonth();
    }
    public static int getCurrentDate() {
        Calendar calendar = Util.getCalendar(new Date());
        return calendar.get(Calendar.DATE);
    }

    public static Date addDays(int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static void setRepeatingAlarm(Context context, AlarmManager alarmManager, int hour, int minute, int frequency) {
        Log.i("UTIL", "Setting repeating alarm in util");

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 123,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // 12:00 AM
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        long frequencyTime = Math.round(AlarmManager.INTERVAL_DAY / frequency);

        //Send notification twice a day
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequencyTime, pendingIntent);

        //Send notification every 5 seconds
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (60   * 1000), pendingIntent);

        //alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), (5 * 1000), pendingIntent);

        //https://developer.android.com/training/scheduling/alarms.html#type
    }

    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    Log.w("setNumberPickerTextCol", e);
                }
                catch(IllegalAccessException e){
                    Log.w("setNumberPickerTextCol", e);
                }
                catch(IllegalArgumentException e){
                    Log.w("setNumberPickerTextC", e);
                }
            }
        }
        return false;
    }

    public static String getNotificationMessageWithTime(Context context, Date dob) {
        boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(context);
        String time = "";
        Integer hours, minutes;
        hours = Storage.getNotificationHours(Util.getSharedPreference());
        minutes = Storage.getNotificationMinutes(Util.getSharedPreference());
        if(is24HourFormat) {
            time = Util.getTwoDigitsString(hours)
                    + ":"
                    + Util.getTwoDigitsString(minutes);
        }
        else {
            if(hours > 12) {
                hours = hours - 12;
                time = Util.getTwoDigitsString(hours)
                        + ":"
                        + Util.getTwoDigitsString(minutes)
                        + "pm";
            }
            else {
                time = Util.getTwoDigitsString(hours)
                        + ":"
                        + Util.getTwoDigitsString(minutes)
                        + "am";
            }
        }
        String message = "You will get notified at "
                + time
                + " on "
                + Util.getStringFromDate(dob, "dd MMM") + " every year";
        return  message;
    }
}

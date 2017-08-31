package com.antandbuffalo.birthdayreminder;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.antandbuffalo.birthdayreminder.database.DBHelper;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;
import com.antandbuffalo.birthdayreminder.database.OptionsDBHelper;
import com.antandbuffalo.birthdayreminder.settings.SettingsModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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
        if(currentDate.get(Calendar.DAY_OF_YEAR) <= birthDate.get(Calendar.DAY_OF_YEAR)) {
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
                String[] lineComponents = strLine.split(" ");

                n1 = lineComponents[0];
                n1 = n1.replace("_", " ");
                d1 = lineComponents[1];
                m1 = lineComponents[2];
                y1 = lineComponents[3];

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
        String createFolderReturn = Util.createEmptyFolder();
        if(!createFolderReturn.equalsIgnoreCase(Constants.FLAG_SUCCESS)) {
            return createFolderReturn;
        }
        File sdcard = Environment.getExternalStorageDirectory();
        List<DateOfBirth> dobs = DateOfBirthDBHelper.selectAll();
        if(dobs == null || dobs.size() == 0) {
            //Toast.makeText(DataHolder.getInstance().getAppContext(), Constants.ERROR_READ_WRITE_1005, Toast.LENGTH_LONG).show();
            return Constants.MSG_NOTHING_TO_BACKUP_DATA_EMPTY;
        }
        long currentMillis = System.currentTimeMillis();
        String fileName = "/" + Constants.FOLDER_NAME + "/" + Constants.FILE_NAME + Constants.FILE_NAME_SUFFIX;
        String fileNameBackup = "/" + Constants.FOLDER_NAME + "/" + Constants.FILE_NAME + "_" + currentMillis + Constants.FILE_NAME_SUFFIX;

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
                myOutWriter.append(name + " " + dobString);
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
            myOutWriter.append(name + " " + dobString);
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
            //DateOfBirthDBHelper.deleteAll();

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

                String[] lineComponents = strLine.split(" ");
                n1 = lineComponents[0];
                n1 = n1.replace("_", " ");
                d1 = lineComponents[1];
                m1 = lineComponents[2];
                y1 = lineComponents[3];

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
}

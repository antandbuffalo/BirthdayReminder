package com.antandbuffalo.birthdayreminder;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by i677567 on 5/10/15.
 */
public class Util {

    public static int convertDPtoPixel(float density, Resources resources) {
        // Get the screen's density scale
        final float scale = resources.getDisplayMetrics().density;
    // Convert the dps to pixels, based on density scale
        return (int) (density * scale + 0.5f);
    }

    public static Date getDateFromString(String input) {
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

    public static void readFromFile() {

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // sdcard not found. read from bundle
            //SD Card not found.
            return;
        }
        try {
            File sdcard = Environment.getExternalStorageDirectory();
            // Get the text file
            File file = new File(sdcard, Constants.FOLDER_NAME + "/" + Constants.FILE_NAME + Constants.FILE_NAME_SUFFIX);
            BufferedReader br = new BufferedReader(new BufferedReader(new FileReader(file)));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null)   {
                // Print the content on the console
                DateOfBirth dob = new DateOfBirth();
                String[] lineComponents = strLine.split(" ");
                dob.setName(lineComponents[0].replace("_", " "));

                String dateStr = lineComponents[1] + " " + lineComponents[2] + " " + lineComponents[3];
                DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_WITH_SPACE);
                dob.setDobDate(dateFormat.parse(dateStr));
                DateOfBirthDBHelper.insertDOB(dob);
            }
            //Close the input stream
            br.close();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Read successful");
    }

    public static void writeToFile() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //throw error sd card not found
            return;
        }
        List<DateOfBirth> dobs = DateOfBirthDBHelper.selectAll();
        long currentMillis = System.currentTimeMillis();
        File sdcard = Environment.getExternalStorageDirectory();
        String fileName = "/" + Constants.FOLDER_NAME + "/" + Constants.FILE_NAME + Constants.FILE_NAME_SUFFIX;
        String fileNameBackup = "/" + Constants.FOLDER_NAME + "/" + Constants.FILE_NAME + "_" + currentMillis + Constants.FILE_NAME_SUFFIX;

        File folder = new File(sdcard + File.separator + Constants.FOLDER_NAME);
        if(!folder.exists()) {
            folder.mkdir();
        }
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

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

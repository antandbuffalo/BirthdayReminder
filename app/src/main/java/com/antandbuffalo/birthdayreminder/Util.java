package com.antandbuffalo.birthdayreminder;

import android.database.Cursor;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by i677567 on 5/10/15.
 */
public class Util {

    public static Date getDateFromString(String input) {
        DateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i("date object", date.toString());
        return date;
    }

    public static String getStringFromDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());
        Log.i("date string", dateFormat.format(date));
        return dateFormat.format(date);
    }

    public static Long persistDate(Date date) {
        if (date != null) {
            return date.getTime();
        }
        return null;
    }

    public static Date loadDate(Cursor cursor, int index) {
        if (cursor.isNull(index)) {
            return null;
        }
        return new Date(cursor.getLong(index));
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static int getAge(Date date) {
        Calendar birthDate = getCalendar(date);
        Calendar currentDate = getCalendar(new Date());
        int age = currentDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        if((birthDate.get(Calendar.MONTH) > currentDate.get(Calendar.MONTH)) || (birthDate.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) && birthDate.get(Calendar.DATE) > currentDate.get(Calendar.DATE))) {
            age--;
        }
        return age;
    }

    public static void writeToFile() {
        List<DateOfBirth> dobs = null;
        String dirPath = "C:/Users/980765/Desktop";
        long fileSuffix = System.currentTimeMillis();
        String fileName = "BirthdayReminder/dob_" + fileSuffix + ".txt";
        File myFile = new File(dirPath, fileName);
        try {
            myFile.createNewFile();

            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            for (DateOfBirth dob : dobs) {
                String name = dob.getName();
                name = name.replace(" ", "_");
                String dobString = dob.getDobDate().getDate()
                        + " "
                        + (dob.getDobDate().getMonth() + 1)
                        + " "
                        + dob.getDobDate().getYear();
                //int originalYear = dob.getYear();
                //myOutWriter.append(name + " " + dobString + " " + originalYear);
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

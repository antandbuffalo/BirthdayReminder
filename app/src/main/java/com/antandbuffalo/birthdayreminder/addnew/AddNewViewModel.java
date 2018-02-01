package com.antandbuffalo.birthdayreminder.addnew;

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddNewViewModel extends ViewModel {

    Calendar cal = Util.getCalendar();
    String name;

    public List getDates(Integer maxValue) {
        List<String> dateList = new ArrayList<String>();
        for (int i = 1; i <= maxValue; i++) {
            dateList.add(i + "");
        }
        return dateList;
    }

    public List getMonths() {
        return Util.getMonths();
    }

    public List getYears(Integer minYear, Integer maxYear) {
        List<String> yearsList = new ArrayList<String>();
        for (int i = minYear; i <= maxYear; i++) {
            yearsList .add(i + "");
        }
        return yearsList;
    }

    public Map<Integer, Integer> getYearsMapper(Integer minYear, Integer maxYear) {
        Map<Integer, Integer> yearsMap = new HashMap<Integer, Integer>();
        Integer counter = maxYear - minYear;
        int j = minYear;
        for (int i=0; i<=counter; i++) {
            Log.i("YEAR", i + " -- " + j);
            yearsMap.put(j++, i);
        }
        return yearsMap;
    }

    

    public Boolean isDOBAvailable(DateOfBirth dob) {
        return !DateOfBirthDBHelper.isUniqueDateOfBirthIgnoreCase(dob);
    }

    public String getFileName(String name) {
        return Util.fileToLoad(name);
    }

    public String loadFromFileWithName(String fileName) {
        return Util.readFromAssetFile(fileName);
    }

    public void saveToDB(String name, Integer year) {
        Date plainDate = cal.getTime();
        DateOfBirth dob = new DateOfBirth();
        dob.setName(name);
        dob.setDobDate(plainDate);
        DateOfBirthDBHelper.insertDOB(dob);
        Util.updateFile(dob);
    }
}

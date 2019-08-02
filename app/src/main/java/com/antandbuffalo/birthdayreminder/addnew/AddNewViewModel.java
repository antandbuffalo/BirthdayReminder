package com.antandbuffalo.birthdayreminder.addnew;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;
import com.antandbuffalo.birthdayreminder.model.BirthdayInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;


public class AddNewViewModel extends ViewModel {

    Calendar cal = Util.getCalendar();
    String name;
    //months starts from 0 for Jan
    Integer date = 1, month = 0, year = Constants.START_YEAR;
    private Boolean isRemoveYear = false;
    private static final int MONTH_FEB = 1;
    DateOfBirth dateOfBirth;

    public void initDefaults() {
//        cal.setTime(new Date());
//        date = cal.get(Calendar.DATE);
//        month = cal.get(Calendar.MONTH);
//        year = cal.get(Calendar.YEAR);
        //year = Constants.REMOVE_YEAR_VALUE;

        isRemoveYear = true;
        name = "";

        dateOfBirth = new DateOfBirth();
    }

    public boolean canMoveToMonth(String date) {
        return date.length() == 2;
    }

    public boolean canMoveToYear(String month) {
        return month.length() == 2;
    }

    public Boolean getRemoveYear() {
        return isRemoveYear;
    }

    public void setRemoveYear(Boolean removeYear) {
        if(removeYear) {
            //year = Constants.REMOVE_YEAR_VALUE;
        }
        isRemoveYear = removeYear;
    }

    public void setName(String givenName) {
        name = givenName.trim();
    }


    public boolean isLeapYear(Integer year) {
        return (year % 4 == 0);
    }

    public Integer getSelectedYear() {
        return year;
    }

    public Integer getSelectedMonth() {
        return month + 1;
    }

    public Integer getSelectedDate() {
        return date;
    }

    public List getDates() {
        List<String> dateList = new ArrayList<String>();
        Integer maxValue = null;
        if(isRemoveYear) {
            if(month == MONTH_FEB) {
                //month is feb and year removed. So assign max value
                maxValue = 29;
            }
        }
        else {
            if(Util.isCurrentYear(year) && Util.isCurrentMonth(month)) {
                maxValue = Util.getCurrentDate();
            }
            else if(isLeapYear(year) && month == MONTH_FEB) {
                //leap year
                maxValue = 29;
            }
        }
        if(maxValue == null) {
            maxValue = Constants.MONTH_DAYS.get(month);
        }

        for (int i = 1; i <= maxValue; i++) {
            dateList.add(i + "");
        }
        return dateList;
    }

    public List getMonths() {
        List<String> allMonths, filteredMonths;
        allMonths = Util.getMonths();
        if(!isRemoveYear && Util.isCurrentYear(year)) {
            filteredMonths = new ArrayList<String>();
            for (int i = 0; i <= Util.getCurrentMonth(); i++) {
                filteredMonths.add(allMonths.get(i));
            }
        }
        else {
            filteredMonths = allMonths;
        }
        return filteredMonths;
    }

    public List getYears() {
        Integer minYear = Constants.START_YEAR, maxYear = cal.get(Calendar.YEAR);
        List<String> yearsList = new ArrayList<String>();
        for (int i = minYear; i <= maxYear; i++) {
            yearsList .add(i + "");
        }
        return yearsList;
    }

    public void setSelectedDate(Integer selectedDate) {
        date = selectedDate;
        if(isRemoveYear && month == MONTH_FEB && date == 29) {
            year = Constants.LEAP_YEAR;
        }
    }

    public void setSelectedMonth(Integer selectedMonth) {
        month = selectedMonth;
    }

    public void setSelectedYear(Integer selectedYear) {
        year = selectedYear;
    }

    public Map<Integer, Integer> getYearsMapper() {
        cal.setTime(new Date());
        Integer minYear = Constants.START_YEAR, maxYear = cal.get(Calendar.YEAR);
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

    public Boolean isNameEmpty() {
        return name.equalsIgnoreCase("");
    }


    public String getFileName() {
        return Util.fileToLoad(name);
    }

    public String loadFromFileWithName(String fileName) {
        return Util.readFromAssetFile(fileName);
    }

    public void setDateOfBirth(BirthdayInfo birthdayInfo) {
        int intDate, intMonth, intYear;

        try {
            intDate = Integer.parseInt(birthdayInfo.date);
            intMonth = Integer.parseInt(birthdayInfo.month);
            intYear = Integer.parseInt(birthdayInfo.year);
            cal.set(intYear, intMonth, intDate);
            Date plainDate = cal.getTime();

            dateOfBirth.setName(name);
            dateOfBirth.setDobDate(plainDate);
            dateOfBirth.setRemoveYear(isRemoveYear);
            dateOfBirth.setAge(Util.getAge(dateOfBirth.getDobDate()));

        }
        catch (Exception e) {
            Log.e("PARSE_INT", e.getLocalizedMessage());
        }
    }

    public void saveToDB() {
        DateOfBirthDBHelper.insertDOB(dateOfBirth);
        //Util.updateFile(dateOfBirth);
    }

    public boolean isValidDateOfBirth(String date, String month, String year) {
        int intDate, intMonth, intYear;
        try {
            intDate = Integer.parseInt(date);
            intMonth = Integer.parseInt(month);
            intYear = Integer.parseInt(year);
            cal.set(intYear, intMonth, intDate);
            Date plainDate = cal.getTime();

            return  true;
        }
        catch (Exception e) {
            Log.e("PARSE_INT", e.getLocalizedMessage());
            return false;
        }
    }

    public void clearInputs() {
        initDefaults();
    }
}

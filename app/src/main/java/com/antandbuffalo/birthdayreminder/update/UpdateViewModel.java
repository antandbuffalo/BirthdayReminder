package com.antandbuffalo.birthdayreminder.update;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

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


public class UpdateViewModel extends ViewModel {

    Calendar cal = Util.getCalendar();
    String name;
    //months starts from 0 for Jan
    Integer date = 1, month = 0, year = Constants.START_YEAR;
    boolean isRemoveYear = false;
    private static final int MONTH_FEB = 1;
    DateOfBirth dateOfBirth;

    public void initDefaults(DateOfBirth givenDateOfBirth) {
        cal.setTime(givenDateOfBirth.getDobDate());

        dateOfBirth = givenDateOfBirth;

        name = givenDateOfBirth.getName();

        date = cal.get(Calendar.DATE);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

        isRemoveYear = givenDateOfBirth.getRemoveYear();
    }

    public void setName(String givenName) {
        name = givenName.trim();
    }

    public boolean isLeapYear(Integer year) {
        return (year % 4 == 0);
    }

    public Integer getSelectedYearPosition() {
        Map<Integer, Integer> yearsMap = getYearsMapper();
        return yearsMap.get(year);
    }

    public Integer getSelectedMonthPosition() {
        return month;
    }

    public Integer getSelectedDatePosition() {
        List dateList = getDates();
        //need to check the max value. Because of different values of month like 28, 29, 30 and 31 days.
        if(date > dateList.size()) {
            date = dateList.size();
        }
        //date starts from 1. So for the position substract 1
        return (date - 1);
    }


    public List getDates() {
        List<String> dateList = new ArrayList<String>();
        Integer maxValue;
        if(isLeapYear(year) && month == MONTH_FEB) {
            //leap year
            maxValue = 29;
        }
        else {
            maxValue = Constants.MONTH_DAYS.get(month);
        }
        for (int i = 1; i <= maxValue; i++) {
            dateList.add(i + "");
        }
        return dateList;
    }

    public List getMonths() {
        return Util.getMonths();
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
        return !DateOfBirthDBHelper.isUniqueDateOfBirth(dob);
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

    public void setDateOfBirth() {
        cal.set(year, month, date);
        Date plainDate = cal.getTime();

        dateOfBirth.setName(name);
        dateOfBirth.setDobDate(plainDate);
        dateOfBirth.setRemoveYear(isRemoveYear);
        dateOfBirth.setAge(Util.getAge(dateOfBirth.getDobDate()));
    }

    public void saveToDB() {
        DateOfBirthDBHelper.insertDOB(dateOfBirth);
        Util.updateFile(dateOfBirth);
    }

    public void delete(long dobId) {
        DateOfBirthDBHelper.deleteRecordForTheId(dobId);
    }

    public void update() {
        DateOfBirthDBHelper.updateDOB(dateOfBirth);
    }

}

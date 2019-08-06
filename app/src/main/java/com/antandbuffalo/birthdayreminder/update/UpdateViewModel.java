package com.antandbuffalo.birthdayreminder.update;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;
import com.antandbuffalo.birthdayreminder.model.BirthdayInfo;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UpdateViewModel extends ViewModel {

    //months starts from 0 for Jan
    private Boolean isRemoveYear = false;
    private static final int MONTH_FEB = 1;
    DateOfBirth dateOfBirth;
    BirthdayInfo birthdayInfo;

    public void initDefaults(DateOfBirth givenDateOfBirth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(givenDateOfBirth.getDobDate());

        dateOfBirth = givenDateOfBirth;
        birthdayInfo = new BirthdayInfo();

        birthdayInfo.name = givenDateOfBirth.getName();
        birthdayInfo.date = new Integer(calendar.get(Calendar.DATE)).toString();
        birthdayInfo.month = new Integer(calendar.get(Calendar.MONTH)).toString();
        birthdayInfo.year = new Integer(calendar.get(Calendar.YEAR)).toString();
        birthdayInfo.isRemoveYear = givenDateOfBirth.getRemoveYear();

        if(givenDateOfBirth.getRemoveYear()) {
            birthdayInfo.year = Constants.REMOVE_YEAR_VALUE.toString();
        }
    }

    public List getMonths() {
        return Util.getMonths();
    }

    public void setName(String givenName) {
        birthdayInfo.name = givenName.trim();
    }

    public Boolean getRemoveYear() {
        return isRemoveYear;
    }

    public void setRemoveYear(Boolean removeYear) {
        if(removeYear) {
            birthdayInfo.year = Constants.REMOVE_YEAR_VALUE.toString();
        }
        isRemoveYear = removeYear;
    }


    public boolean isLeapYear(Integer year) {
        return (year % 4 == 0);
    }

    public Integer getSelectedMonthPosition() {
        int month = Integer.parseInt(birthdayInfo.month);
        List monthsList = getMonths();
        if(month > monthsList.size() - 1) {
            month = monthsList.size() - 1;
        }
        return month;
    }

    public void setSelectedMonth(Integer selectedMonth) {
        birthdayInfo.month = selectedMonth.toString();
    }

    public Map<Integer, Integer> getYearsMapper() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Integer minYear = Constants.START_YEAR, maxYear = calendar.get(Calendar.YEAR);
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
        return birthdayInfo.name.equalsIgnoreCase("");
    }

    public Boolean setDateOfBirth() {
        Calendar calendar = Calendar.getInstance();
        int intDate, intMonth, intYear;

        birthdayInfo.year = birthdayInfo.isRemoveYear? Constants.REMOVE_YEAR_VALUE.toString() : birthdayInfo.year;
        dateOfBirth.setName(birthdayInfo.name);

        try {
            intDate = Integer.parseInt(birthdayInfo.date);
            intMonth = Integer.parseInt(birthdayInfo.month);
            intYear = Integer.parseInt(birthdayInfo.year);
            calendar.set(intYear, intMonth, intDate);
            Date plainDate = calendar.getTime();

            dateOfBirth.setDobDate(plainDate);
            dateOfBirth.setRemoveYear(birthdayInfo.isRemoveYear);
            dateOfBirth.setAge(Util.getAge(dateOfBirth.getDobDate()));
            return true;
        }
        catch (Exception e) {
            Log.e("PARSE_INT", e.getLocalizedMessage());
            return false;
        }
    }

    public void delete(long dobId) {
        DateOfBirthDBHelper.deleteRecordForTheId(dobId);
    }

    public void update() {
        DateOfBirthDBHelper.updateDOB(dateOfBirth);
    }

}

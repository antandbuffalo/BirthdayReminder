package com.antandbuffalo.birthdayreminder.addnew;

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;

import java.util.Calendar;
import java.util.Date;


public class AddNewViewModel extends ViewModel {

    Calendar cal = Util.getCalendar();
    String name;

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

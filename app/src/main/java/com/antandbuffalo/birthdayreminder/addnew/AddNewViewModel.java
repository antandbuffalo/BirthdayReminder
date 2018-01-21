package com.antandbuffalo.birthdayreminder.addnew;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.antandbuffalo.birthdayreminder.DateOfBirth;

/**
 * Created by Jeyabalaji on 20/1/18.
 */

public class AddNewViewModel extends ViewModel {

    private LiveData<Integer> day, month, year;

    public LiveData<Integer> getDay() {
        return day;
    }

    public void setDay(LiveData<Integer> day) {
        this.day = day;
    }

    public LiveData<Integer> getMonth() {
        return month;
    }

    public void setMonth(LiveData<Integer> month) {
        this.month = month;
    }

    public LiveData<Integer> getYear() {
        return year;
    }

    public void setYear(LiveData<Integer> year) {
        this.year = year;
    }
}

package com.antandbuffalo.birthdayreminder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by i677567 on 23/9/15.
 */
public class DateOfBirth {
    private int id;
    private String name;
    private String description;
    private Date dob;
    private int age;
    private int year;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}

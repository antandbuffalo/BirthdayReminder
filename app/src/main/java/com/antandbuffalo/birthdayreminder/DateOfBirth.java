package com.antandbuffalo.birthdayreminder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by i677567 on 23/9/15.
 */
public class DateOfBirth {
    private long dobId;
    private String name;
    private Date dobDate;
    private String description;
    private int age;

    public long getDobId() {
        return dobId;
    }

    public void setDobId(long dobId) {
        this.dobId = dobId;
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

    public Date getDobDate() {
        return dobDate;
    }

    public void setDobDate(Date dobDate) {
        this.dobDate = dobDate;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}

package com.antandbuffalo.birthdayreminder.settings;

import java.util.Date;

/**
 * Created by i677567 on 21/12/15.
 */
public class SettingsModel {
    private String key;
    private String title;
    private String subTitle;
    private Date updatedOn;
    private String type;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public static SettingsModel newInstance() {
        return new SettingsModel();
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

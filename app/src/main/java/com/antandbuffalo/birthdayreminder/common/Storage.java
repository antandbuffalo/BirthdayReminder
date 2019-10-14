package com.antandbuffalo.birthdayreminder.common;

import android.content.SharedPreferences;

import com.antandbuffalo.birthdayreminder.Constants;

/**
 * Created by i677567 on 5/10/15.
 */
public class Storage {

    public static Integer getNotificationFrequency(SharedPreferences preferences) {
        return preferences.getInt(Constants.PREFERENCE_NOTIFINCATION_FREQUENCY, 1);
    }

    public static Integer getFeaturesNotificationStatus(SharedPreferences preferences, String key) {
        return preferences.getInt(key, 0);
    }

    public static void setFeaturesNotificationStatus(SharedPreferences preferences, String key, Integer value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static Integer getNotificationHours(SharedPreferences preferences) {
        return preferences.getInt(Constants.PREFERENCE_NOTIFICATION_TIME_HOURS, 0);
    }

    public static Integer getNotificationMinutes(SharedPreferences preferences) {
        return preferences.getInt(Constants.PREFERENCE_NOTIFICATION_TIME_MINUTES, 0);
    }
}

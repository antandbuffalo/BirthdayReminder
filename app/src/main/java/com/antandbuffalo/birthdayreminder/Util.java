package com.antandbuffalo.birthdayreminder;

import android.database.Cursor;

import java.util.Date;

/**
 * Created by i677567 on 5/10/15.
 */
public class Util {
    public static Long persistDate(Date date) {
        if (date != null) {
            return date.getTime();
        }
        return null;
    }

    public static Date loadDate(Cursor cursor, int index) {
        if (cursor.isNull(index)) {
            return null;
        }
        return new Date(cursor.getLong(index));
    }
}

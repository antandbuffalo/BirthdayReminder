package com.antandbuffalo.birthdayreminder;

/**
 * Created by i677567 on 14/10/15.
 */
public class Constants {

    // Database Name
    public static final String DATABASE_NAME = "BirthdayReminder";
    public static final int DATABASE_VERSION = 1;

    // Contacts table name
    public static final String TABLE_DATE_OF_BIRTH = "DATE_OF_BIRTH";

    // Contacts Table Columns names
    public static final String COLUMN_DOB_ID = "DOB_ID";
    public static final String COLUMN_DOB_NAME = "NAME";
    public static final String COLUMN_DOB_DATE = "DOB_DATE";
    public static final String KEY_DOB_EXTRA1 = "EXTRA1";


    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_WITH_SPACE = "dd MM yyyy";
    public static final String DAY_OF_YEAR = "MMdd";

    public static final String SPACE_REPLACER = "_";
    public static final String FOLDER_NAME = "BirthdayReminder";
    public static final String FILE_NAME = "dob";
    public static final String FILE_NAME_SUFFIX = ".txt";

    public static final String CIRCLE_BG_TODAY = "#1B5E20";
    public static final String CIRCLE_BG_DEFAULT = "#795548";

    public static final int RECENT_DURATION = 3;

    public static final int ADD_NEW_MEMBER = 1;

    public static final String NOTIFICATION_ADD_MEMBER_SUCCESS = "Successfully added";

    public static final String IS_USER_ADDED = "IS_USER_ADDED";

    public static final String FLAG_SUCCESS = "TRUE";
    public static final String FLAG_FAILURE = "FALSE";
}

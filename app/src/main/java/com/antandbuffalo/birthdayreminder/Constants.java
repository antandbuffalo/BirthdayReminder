package com.antandbuffalo.birthdayreminder;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by i677567 on 14/10/15.
 */
public class Constants {

    // Database Name
    public static final String DATABASE_NAME = "BirthdayReminder";
    public static final int DATABASE_VERSION = 1;

    // Contacts table name
    public static final String TABLE_DATE_OF_BIRTH = "DATE_OF_BIRTH";
    public static final String TABLE_OPTIONS = "OPTIONS";

    // Contacts Table Columns names
    public static final String COLUMN_DOB_ID = "DOB_ID";
    public static final String COLUMN_DOB_NAME = "NAME";
    public static final String COLUMN_DOB_DATE = "DOB_DATE";
    public static final String KEY_DOB_EXTRA1 = "EXTRA1";

    public static final String COLUMN_OPTION_CODE = "OPTION_CODE";
    public static final String COLUMN_OPTION_TITLE = "TITLE";
    public static final String COLUMN_OPTION_SUBTITLE = "SUBTITLE";
    public static final String COLUMN_OPTION_UPDATED_ON = "UPDATED_ON";

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String ADD_NEW_DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_FORMAT_WITH_SPACE = "dd MM yyyy";
    public static final String DAY_OF_YEAR = "MMdd";
    public static final String FULL_DAY_OF_YEAR = "yyyyMMdd";

    public static final String SPACE_REPLACER = "_";
    public static final String FOLDER_NAME = "BirthdayReminder";
    public static final String FILE_NAME = "dob";
    public static final String FILE_NAME_SUFFIX = ".txt";

    public static final String CIRCLE_BG_TODAY = "#1B5E20";
    public static final String CIRCLE_BG_DEFAULT = "#795548";

    public static final int RECENT_DURATION = 3;
    public static final int HEALTHY_BACKUP_DURATION = 30;
    public static final int AVERAGE_BACKUP_DURATION = 365;

    public static final int ADD_NEW_MEMBER = 1;
    public static final int DELETE_MEMBER = 2;

    public static final String IS_USER_ADDED = "IS_USER_ADDED";

    public static final String FLAG_SUCCESS = "TRUE";
    public static final String FLAG_FAILURE = "FALSE";

    public static final String SETTINGS_WRITE_FILE = "WRITE_FILE";
    public static final String SETTINGS_READ_FILE = "READ_FILE";
    public static final String SETTINGS_DELETE_ALL = "DELETE_ALL";
    public static final String SETTINGS_ABOUT = "ABOUT";

    public static final int SETTINGS_CELL_TYPE_DATE = 0;
    public static final int SETTINGS_CELL_TYPE_1_LETTER = 1;
    public static final int SETTINGS_CELL_TYPE_NA = 2;

    public static final int SETTINGS_CELL_TYPES_COUNT = 3;

    public static final Set<String> SETTINGS_CELL_TYPE_0_VALUES = new HashSet<String>() {{
        add(SETTINGS_READ_FILE);
        add(SETTINGS_WRITE_FILE);
    }};

    public static final Set<String> SETTINGS_CELL_TYPE_1_LETTER_VALUES = new HashSet<String>() {{
        add(SETTINGS_DELETE_ALL);
        add(SETTINGS_ABOUT);
    }};

    public static final String SETTINGS_WRITE_FILE_TITLE = "Take a backup";
    public static final String SETTINGS_READ_FILE_TITLE = "Load from latest backup";
    public static final String SETTINGS_DELETE_ALL_TITLE = "Delete All";

    public static final String SETTINGS_WRITE_FILE_SUB_TITLE = "Till now no backup files created";
    public static final String SETTINGS_READ_FILE_SUB_TITLE = "Till now not loaded from any backup file";

    public static final String ERROR_NO_SD_CARD = "Not able to read the backup file. SD Card not found";
    public static final String ERROR_NO_BACKUP_FILE = "Backup file is not found";
    public static final String ERROR_UNKNOWN = "Unkown error occured";
    public static final String ERR_SD_CARD_NOT_FOUND = "SD Card not found. Not able to take backup";
    public static final String MSG_NOTHING_TO_BACKUP_DATA_EMPTY = "Nothing to backup. Data is empty";

    public static final String NOTIFICATION_READ_WRITE_1001 = "Data backup successful";
    public static final String NOTIFICATION_SUCCESS_DATA_LOAD = "Data loaded successfully";

    public static final String NOTIFICATION_DELETE_1001 = "All Datas deleted";
    public static final String NOTIFICATION_ADD_MEMBER_SUCCESS = "Successfully added";
    public static final String NOTIFICATION_DELETE_MEMBER_SUCCESS = "Successfully Deleted";
    public static final String NOTIFICATION_UPDATE_MEMBER_SUCCESS = "Successfully Updated";

    public static final String NAME_EMPTY = "Please enter Name";
    public static final String ERROR = "Error";
    public static final String USER_EXIST = "Same Date of Birth already available";
    public static final String OK = "OK";

    public static final String PREFERENCE_NAME = "BirthdayReminder";
    public static final String TYPE_ADD_NEW = "AddNew";
}

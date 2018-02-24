package com.antandbuffalo.birthdayreminder.restore;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DataHolder;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;
import com.antandbuffalo.birthdayreminder.settings.SettingsModel;

import java.util.Calendar;
import java.util.Date;

public class RestoreBackup extends Activity {
    Intent intent = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restore_backup);

        ImageButton restoreOk = (ImageButton) findViewById(R.id.save);
        restoreOk.setBackgroundResource(R.drawable.save_button);

        ImageButton restoreCancel = (ImageButton)findViewById(R.id.cancel);
        restoreCancel.setBackgroundResource(R.drawable.cancel_button);

        intent = new Intent();
        intent.putExtra(Constants.IS_USER_ADDED, Constants.FLAG_FAILURE.toString());
        setResult(RESULT_OK, intent);

        restoreOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getStoragePermission(Constants.MY_PERMISSIONS_READ_EXTERNAL_STORAGE)) {
                    restoreBackup(true);
                }
            }
        });

        restoreCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(Constants.IS_USER_ADDED, Constants.FLAG_FAILURE.toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    public void restoreBackup(Boolean isGranted) {
        if(isGranted) {
            Util.readFromFile(Constants.FILE_NAME);

            intent.putExtra(Constants.IS_USER_ADDED, Constants.FLAG_FAILURE.toString());
            setResult(RESULT_OK, intent);
            Toast toast = Toast.makeText(getApplicationContext(), Constants.NOTIFICATION_SUCCESS_DATA_LOAD, Toast.LENGTH_SHORT);
            toast.show();

            SettingsModel datum = SettingsModel.newInstance();
            datum.setKey(Constants.SETTINGS_READ_FILE);
            datum.setTitle(Constants.SETTINGS_READ_FILE_TITLE);
            datum.setSno(2);
            Util.updateRestoreTime(datum);
            finish();
        }
        else {
            Toast.makeText(this, "Please provide storage access to read the backup file", Toast.LENGTH_LONG).show();
        }
    }
    public Boolean getStoragePermission(int permissionType) {
        switch (permissionType) {
            case Constants.MY_PERMISSIONS_READ_EXTERNAL_STORAGE: {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(RestoreBackup.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constants.MY_PERMISSIONS_READ_EXTERNAL_STORAGE);

                    return false;
                } else {
                    // Permission has already been granted
                    return true;
                }
            }

        }
        return false;
    }
    @Override
    public void onBackPressed() {
        intent.putExtra(Constants.IS_USER_ADDED, Constants.FLAG_FAILURE.toString());
        setResult(RESULT_OK, intent);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    restoreBackup(true);

                } else {
                    // permission denied, boo! Disable the
                    restoreBackup(false);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
}


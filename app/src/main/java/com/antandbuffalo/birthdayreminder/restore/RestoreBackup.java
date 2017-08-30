package com.antandbuffalo.birthdayreminder.restore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
                Util.readFromFile(Constants.FILE_NAME);
//                for (int i = 0; i < DataHolder.getInstance().refreshTracker.size(); i++) {
//                    DataHolder.getInstance().refreshTracker.set(i, true);
//                }
                intent.putExtra(Constants.IS_USER_ADDED, Constants.FLAG_FAILURE.toString());
                setResult(RESULT_OK, intent);
                Toast toast = Toast.makeText(getApplicationContext(), Constants.NOTIFICATION_SUCCESS_DATA_LOAD, Toast.LENGTH_SHORT);
                toast.show();
                finish();
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
    @Override
    public void onBackPressed() {
        intent.putExtra(Constants.IS_USER_ADDED, Constants.FLAG_FAILURE.toString());
        setResult(RESULT_OK, intent);
    }
}

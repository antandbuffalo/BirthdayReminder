package com.antandbuffalo.birthdayreminder.notificationtime;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class NotificationTime extends Activity {
    Intent intent;
    boolean is24HourFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_time);

        final SharedPreferences settings = Util.getSharedPreference();
        intent = new Intent();

        TimePicker picker=(TimePicker)findViewById(R.id.notificationTimePicker);
        is24HourFormat = android.text.format.DateFormat.is24HourFormat(this);
        picker.setIs24HourView(is24HourFormat);

        ImageButton save = findViewById(R.id.save);
        save.setBackgroundResource(R.drawable.save_button);

        ImageButton cancel = findViewById(R.id.cancel);
        cancel.setBackgroundResource(R.drawable.cancel_button);

        populateInitialValues(picker, settings.getInt(Constants.PREFERENCE_NOTIFICATION_TIME_HOURS, 0), settings.getInt(Constants.PREFERENCE_NOTIFICATION_TIME_MINUTES, 0), is24HourFormat);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour, minute;
                if (Build.VERSION.SDK_INT >= 23 ){
                    hour = picker.getHour();
                    minute = picker.getMinute();
                }
                else{
                    hour = picker.getCurrentHour();
                    minute = picker.getCurrentMinute();
                }
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                int frequency =  settings.getInt(Constants.PREFERENCE_NOTIFINCATION_FREQUENCY, 1);
                Util.setRepeatingAlarm(getApplicationContext(), alarmManager, hour, minute, frequency);

                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(Constants.PREFERENCE_NOTIFICATION_TIME_HOURS, hour);
                editor.putInt(Constants.PREFERENCE_NOTIFICATION_TIME_MINUTES, minute);
                editor.commit();
                Toast toast = Toast.makeText(getApplicationContext(), "Notification Time updated successfully", Toast.LENGTH_SHORT);
                toast.show();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        loadAd();
    }

    public void populateInitialValues(TimePicker timePicker, int hours, int minutes, boolean is24Hours) {
        if (Build.VERSION.SDK_INT >= 23 ){
            timePicker.setHour(hours);
            timePicker.setMinute(minutes);
        }
        else {
            timePicker.setCurrentHour(hours);
            timePicker.setCurrentMinute(minutes);
        }
        timePicker.setIs24HourView(is24Hours);
    }

    public void loadAd() {
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

}

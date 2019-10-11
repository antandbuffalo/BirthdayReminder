package com.antandbuffalo.birthdayreminder.notificationfrequency;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.common.Storage;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class NotificationFrequency extends Activity {
    Intent intent;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_frequency);

        settings = Util.getSharedPreference();

        intent = new Intent();
        final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.notificationFrequency);
        Util.setNumberPickerTextColor(numberPicker, Color.WHITE);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setMaxValue(24);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(false);

        ImageButton save = (ImageButton) findViewById(R.id.save);
        save.setBackgroundResource(R.drawable.save_button);

        ImageButton cancel = (ImageButton)findViewById(R.id.cancel);
        cancel.setBackgroundResource(R.drawable.cancel_button);

        populateSpinnerFrequency(numberPicker);

        int hours = settings.getInt(Constants.PREFERENCE_NOTIFICATION_TIME_HOURS, 0);
        int minutes = settings.getInt(Constants.PREFERENCE_NOTIFICATION_TIME_MINUTES, 0);
        boolean is24HourFormat = android.text.format.DateFormat.is24HourFormat(this);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Util.setRepeatingAlarm(getApplicationContext(), alarmManager, hours, minutes, numberPicker.getValue());

                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(Constants.PREFERENCE_NOTIFINCATION_FREQUENCY, numberPicker.getValue());
                editor.commit();
                Toast toast = Toast.makeText(getApplicationContext(), "Number of Notifications per day updated Successfully", Toast.LENGTH_SHORT);
                toast.show();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        loadAd();
    }

    public void populateSpinnerFrequency(NumberPicker numberPicker) {
        numberPicker.setValue(Storage.getNotificationFrequency(settings));
    }

    public void loadAd() {
        AdView mAdView = this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}

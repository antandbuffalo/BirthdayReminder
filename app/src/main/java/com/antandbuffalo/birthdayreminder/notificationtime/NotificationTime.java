package com.antandbuffalo.birthdayreminder.notificationtime;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class NotificationTime extends Activity {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_time);

        final SharedPreferences settings = Util.getSharedPreference();
        intent = new Intent();

        final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.notificationTimeHrs);
        Util.setNumberPickerTextColor(numberPicker, Color.WHITE);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setMaxValue(23);
        numberPicker.setMinValue(0);
        numberPicker.setWrapSelectorWheel(false);

        ImageButton save = findViewById(R.id.save);
        save.setBackgroundResource(R.drawable.save_button);

        ImageButton cancel = findViewById(R.id.cancel);
        cancel.setBackgroundResource(R.drawable.cancel_button);


        loadAd();
    }

    public void loadAd() {
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

}

package com.antandbuffalo.birthdayreminder.notification;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.antandbuffalo.birthdayreminder.DataHolder;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.common.Storage;

public class BootComplete extends BroadcastReceiver {
    public BootComplete() {
    }
    AlarmManager am;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // setOneTimeAlarm();
        System.out.println("inside boot complete");
        setRepeatingAlarm(context);
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setRepeatingAlarm(Context context) {
        DataHolder.getInstance().setAppContext(context);

//        Intent intent = new Intent(context, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
//                intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        Calendar calendar = Calendar.getInstance();
//        // 9 AM
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//
//        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                AlarmManager.INTERVAL_HALF_DAY, pendingIntent);
        // am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
        // (5 * 1000), pendingIntent);

        SharedPreferences preference = Util.getSharedPreference();
        Integer hours = Storage.getNotificationHours(preference);
        Integer minutes = Storage.getNotificationMinutes(preference);
        Integer frequency = Storage.getNotificationFrequency(preference);
        Util.setRepeatingAlarm(context, am, hours, minutes, frequency);
    }
}

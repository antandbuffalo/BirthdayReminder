package com.antandbuffalo.birthdayreminder.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.antandbuffalo.birthdayreminder.Constants;
import com.antandbuffalo.birthdayreminder.DataHolder;
import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.MainActivity;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.Util;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;

import java.util.Date;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    NotificationManager notificationManager;
    public AlarmReceiver() {
    }
  
    public void showPreNotifications(Context context, int preNotifDays) {
        Date futureDate = Util.addDays(preNotifDays);
        List<DateOfBirth> preNotifList = DateOfBirthDBHelper.selectForTheDate(context, futureDate);
        if(preNotifList == null || preNotifList.size() == 0) {
            return;
        }
        CharSequence from = "Birthday - Upcoming in " + preNotifDays + " days";
        if(preNotifDays == 1) {
            from = "Birthday - Upcoming in " + preNotifDays + " day";
        }

        CharSequence message = "";
        String sep = "";

        for (DateOfBirth dateOfBirth : preNotifList) {
            message = message + sep + dateOfBirth.getName();
            sep = ", ";
        }

        //notification opening intent
        Intent resultingIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, resultingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_notification).setContentTitle(from).setContentText(message);
        mBuilder.setColor(Color.argb(255, 121, 85, 72));
        mBuilder.setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        int notificationId = 102;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, mBuilder.build());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        DataHolder.getInstance().setAppContext(context);
        System.out.println("inside schedule receiver");
        final SharedPreferences settings = Util.getSharedPreference();
        int preNotifDays = settings.getInt(Constants.PREFERENCE_PRE_NOTIFICATION_DAYS, 0);
        if(preNotifDays > 0) {
            showPreNotifications(context, preNotifDays);
        }
        List<DateOfBirth> todayList = DateOfBirthDBHelper.selectToday(context);
        if(todayList == null || todayList.size() == 0) {
            return;
        }
        CharSequence from = "Birthday Today";
        CharSequence message = "";
        String sep = "";

        for (DateOfBirth dateOfBirth : todayList) {
            message = message + sep + dateOfBirth.getName();
            sep = ", ";
        }

        //notification opening intent
        Intent resultingIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, resultingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_notification).setContentTitle(from).setContentText(message);
        mBuilder.setColor(Color.argb(255, 121, 85, 72));
        mBuilder.setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        int notificationId = 101;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, mBuilder.build());
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}

package com.antandbuffalo.birthdayreminder.notification;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.antandbuffalo.birthdayreminder.DateOfBirth;
import com.antandbuffalo.birthdayreminder.MainActivity;
import com.antandbuffalo.birthdayreminder.R;
import com.antandbuffalo.birthdayreminder.database.DateOfBirthDBHelper;

import java.sql.Date;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    NotificationManager notificationManager;
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        System.out.println("inside schedule receiver");
        List<DateOfBirth> todayList = DateOfBirthDBHelper.selectToday(context);
        if(todayList == null || todayList.size() == 0) {
            return;
        }
        CharSequence from = "Birthday";
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
        mBuilder.setContentIntent(contentIntent);
        int notificationId = 101;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, mBuilder.build());
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}

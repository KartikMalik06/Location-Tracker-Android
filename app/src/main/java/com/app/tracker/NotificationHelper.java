package com.app.tracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NotificationHelper {
    public static final String GROUP_SERVICE = "GROUP_SERVICE";
    public static final String GROUP_EVENTS = "GROUP_EVENTS";
    public static final String GROUP_UNIVERSAL = "GROUP_UNIVERSAL";

    public static Notification getNotificationBuilder(Context context, boolean isOnGoingEvent, String message, Class aClass, Bundle bundle, String groupName ) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null)
            return null;
           String CHANNEL_ID_SERVICE = "CHANNEL_ID_SERVICE";

        Intent intent = new Intent(context, aClass);
        if (bundle != null)
            intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID_SERVICE, CHANNEL_ID_SERVICE, importance);
            notificationManager.createNotificationChannel(mChannel);
            Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID_SERVICE)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .setStyle(new Notification.BigTextStyle().bigText(message))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            builder.setChannelId(CHANNEL_ID_SERVICE);
            builder.setAutoCancel(!isOnGoingEvent);
            builder.setOngoing(isOnGoingEvent);
            notification = builder.build();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
            } else {
                builder.setSmallIcon(R.mipmap.ic_launcher);
            }
            builder.setOngoing(isOnGoingEvent)
                    .setAutoCancel(!isOnGoingEvent)
                    .setGroup(groupName);
            notification = builder.build();
        }
        return notification;
    }

    public static void cancel(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }
}

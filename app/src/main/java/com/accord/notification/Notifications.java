package com.accord.notification;


import static com.accord.App.CHANNEL_PRIVATE_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.accord.MainActivity;
import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.model.Message;

public class Notifications {
    public static void sendOnPrivateChannel(ModelBuilder builder, Message message) {
        // Create an explicit intent for an Activity in your app
        Intent activityIntent = new Intent(builder.getMainActivity(), MainActivity.class);
//        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(builder.getMainActivity(), 0, activityIntent, 0);

        Intent broadcastIntent = new Intent(builder.getMainActivity(), Notifications.class);
        broadcastIntent.putExtra("toastMessage", message.getMessage());
        PendingIntent actionIntent = PendingIntent.getBroadcast(builder.getMainActivity(), 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(builder.getMainActivity(), CHANNEL_PRIVATE_ID)
                .setSmallIcon(R.drawable.accord_logo)
                .setContentTitle(message.getFrom())
                .setContentText(message.getMessage())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.BLUE)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
                .build();

        builder.getNotificationManager().notify(message.getNotificationId(), notification);
    }
}

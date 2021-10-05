package com.accord.notification;


import static com.accord.App.CHANNEL_PRIVATE_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.accord.MainActivity;
import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.model.Message;
import com.accord.model.PrivateChat;
import com.accord.model.ServerChannel;

public class Notifications {
    public static void sendOnPrivateChannel(ModelBuilder builder, PrivateChat privateChat, Message message) {
        // open chat
        // Create an explicit intent for an Activity in your app with will be called
        Intent chatActivityIntent = new Intent(builder.getMainActivity(), NotificationHandler.class);
//        chatActivityIntent.putExtra("action", "openChat");
        chatActivityIntent.setAction("openChat");
        PendingIntent chatIntent = PendingIntent.getBroadcast(builder.getMainActivity(), 0, chatActivityIntent, 0);

        // set read messages in chat
        // Create an explicit intent for an Activity in your app with will be called
        Intent readActivityIntent = new Intent(builder.getMainActivity(), NotificationHandler.class);
//        readActivityIntent.putExtra("action", "readChat");
        readActivityIntent.setAction("readChat");

        PendingIntent readIntent = PendingIntent.getBroadcast(builder.getMainActivity(), 0, readActivityIntent, 0);

        // direct answer to chat
        RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply")
                .setLabel("Your answer...") // hint in edittext
                .build();
        Intent replyIntent = new Intent(builder.getMainActivity(), NotificationHandler.class);
//        replyIntent.putExtra("action", "replyToChat");
        replyIntent.setAction("replyToChat");
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(builder.getMainActivity(), 0, replyIntent, 0);
        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_action_password,
                "Reply",
                replyPendingIntent
        ).addRemoteInput(remoteInput).build();

//        Intent broadcastIntent = new Intent(builder.getMainActivity(), Notifications.class);
//        broadcastIntent.putExtra("toastMessage", message.getMessage());
//        PendingIntent actionIntent = PendingIntent.getBroadcast(builder.getMainActivity(), 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(builder.getMainActivity(), CHANNEL_PRIVATE_ID)
                .setSmallIcon(R.drawable.accord_logo)
                .setContentTitle(message.getFrom())
                .setContentText(message.getMessage())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.BLUE)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(chatIntent)
                .addAction(R.mipmap.ic_launcher, "Als gelesen makieren", readIntent)
                .addAction(replyAction)
                .setAutoCancel(true)
                .build();

        builder.getNotificationManager().notify(message.getNotificationId(), notification);
    }

    public static void sendOnServerChannel(ModelBuilder builder, ServerChannel serverChannel, Message message) {
        // Create an explicit intent for an Activity in your app
        Intent activityIntent = new Intent(builder.getMainActivity(), MainActivity.class);
//        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(builder.getMainActivity(), 0, activityIntent, 0);

        // direct answer
        // label = hint in edittext
        RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply")
                .setLabel("Your answer...") // hint in edittext
                .build();
        Intent replyIntent = new Intent(builder.getMainActivity(), NotificationHandler.class);
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(builder.getMainActivity(), 0, replyIntent, 0);
        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.drawable.ic_action_password,
                "Reply",
                replyPendingIntent
        ).addRemoteInput(remoteInput).build();

        // groupChat notification with messages
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(message.getFrom());
        messagingStyle.setConversationTitle(serverChannel.getName());

        // add messages to notification
        for (Message channelMessage : serverChannel.getMessage()) {
            NotificationCompat.MessagingStyle.Message notificationMessage = new NotificationCompat.MessagingStyle.Message(
                    channelMessage.getMessage(),
                    channelMessage.getTimestamp(),
                    channelMessage.getFrom()
            );
            messagingStyle.addMessage(notificationMessage);
        }

        Intent broadcastIntent = new Intent(builder.getMainActivity(), Notifications.class);
        broadcastIntent.putExtra("toastMessage", message.getMessage());
        PendingIntent actionIntent = PendingIntent.getBroadcast(builder.getMainActivity(), 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(builder.getMainActivity(), CHANNEL_PRIVATE_ID)
                .setSmallIcon(R.drawable.accord_logo)
                .setContentTitle(message.getFrom())
                .setContentText(message.getMessage())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.RED)
                // to set messages directly to notification
                .setStyle(messagingStyle)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .addAction(R.mipmap.ic_launcher, "Als gelesen makieren", actionIntent)
                .addAction(replyAction)
                .build();

        builder.getNotificationManager().notify(message.getNotificationId(), notification);
    }
}

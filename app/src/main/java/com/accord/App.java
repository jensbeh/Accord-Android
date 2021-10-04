package com.accord;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String CHANNEL_PRIVATE_ID = "privateChannel";
    private static final String CHANNEL_SERVER_ID = "serverChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        // notification
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // channel 1
            NotificationChannel privateChannel = new NotificationChannel(
                    CHANNEL_PRIVATE_ID,
                    "Private Messages",
                    NotificationManager.IMPORTANCE_HIGH
            );
//            privateChannel.setDescription("Private Messages");

            // channel 2
            NotificationChannel serverChannel = new NotificationChannel(
                    CHANNEL_SERVER_ID,
                    "Server Messages",
                    NotificationManager.IMPORTANCE_HIGH
            );
//            serverChannel.setDescription("Server Messages");

            // manager
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(privateChannel);
            manager.createNotificationChannel(serverChannel);
        }
    }
}

package com.accord.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.accord.ModelBuilder;

public class NotificationHandler extends BroadcastReceiver {
    static ModelBuilder builder;
    public static void setBuilder(ModelBuilder builder) {
        NotificationHandler.builder = builder;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        String message = intent.getStringExtra("toastMessage");
//        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
//
//        if (remoteInput != null) {
//            CharSequence replyText = remoteInput.getCharSequence("key_text_reply");
//            Message answer = new Message().setMessage(String.valueOf(replyText)).setFrom(null);
//            Notifications.sendOnPrivateChannel();
//        }

        String action = intent.getAction();
        switch (action) {
            case "openChat":
                openChat();
                break;
            case "readChat":
                setMessagesAsRead();
                break;
            case "replyToChat":
                sendReply();
                break;
        }
    }

    private void openChat() {
        Toast.makeText(builder.getMainActivity(), "openChat", Toast.LENGTH_SHORT).show();
    }

    private void setMessagesAsRead() {
        Toast.makeText(builder.getMainActivity(), "setMessagesAsRead", Toast.LENGTH_SHORT).show();
    }

    private void sendReply() {
        Toast.makeText(builder.getMainActivity(), "sendReply", Toast.LENGTH_SHORT).show();
    }
}

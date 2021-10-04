package com.accord.net.webSocket.chatSockets;


import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.widget.Toast;

import com.accord.ModelBuilder;
import com.accord.model.Categories;
import com.accord.model.Message;
import com.accord.model.Server;
import com.accord.model.ServerChannel;
import com.accord.net.webSocket.CustomWebSocketConfigurator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;


public class ServerChatWebSocket extends Endpoint {
    private final ModelBuilder builder;
    private final Server server;
    private final SimpleDateFormat timeFormatter;

    private Session session;
    private Timer noopTimer;
    public static final String COM_NOOP = "noop";

    @SuppressLint("SimpleDateFormat")
    public ServerChatWebSocket(ModelBuilder modelBuilder, Server server, URI endpoint) {
        this.builder = modelBuilder;
        this.server = server;
        this.noopTimer = new Timer();
        this.timeFormatter = new SimpleDateFormat("HH:mm");

        try {
            // "Run in NetworkThread" deactivated
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            ClientEndpointConfig clientConfig = ClientEndpointConfig.Builder.create()
                    .configurator(new CustomWebSocketConfigurator(modelBuilder.getPersonalUser().getUserKey()))
                    .build();

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, clientConfig, endpoint);
        } catch (Exception e) {
            System.err.println("Error during establishing websocket connection:");
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        // Store session
        this.session = session;
        // add MessageHandler
        this.session.addMessageHandler(String.class, this::onMessage);

        this.noopTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Send NOOP Message
                try {
                    sendMessage(COM_NOOP);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000 * 30);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
        // cancel timer
        this.noopTimer.cancel();
        // set session null
        this.session = null;
        Toast.makeText(builder.getMainActivity(), "NORMAL_CLOSURE", Toast.LENGTH_LONG).show();
    }

    private void onMessage(String message) {
        try {
            // Process Message
            JSONObject jsonObject = new JSONObject(message);
            // Use callback to handle it
            this.handleMessage(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) throws IOException {
        // check if session is still open
        if (this.session != null && this.session.isOpen()) {
            // send message
            this.session.getBasicRemote().sendText(message);
            this.session.getBasicRemote().flushBatch();
        }
    }

    public void stop() throws IOException {
        // cancel timer
        this.noopTimer.cancel();
        // close session
        this.session.close();
    }

    public Session getSession() {
        return session;
    }

    public void handleMessage(JSONObject msg) {
        try {
            System.out.println("privateChatWebSocketClient");
            System.out.println(msg);

            if (msg.has("channel")) {
                receivedServerMessage(msg);
            }
            if (msg.has("action") && msg.getString("action").equals("info")) {
//                showServerChatAlert(jsonObject);
            }

//            if (msg.has("channel") && msg.getString("channel").equals("private")) {
//                Message message;
//                String channelName;
//                Boolean newChat = true;
//
//                Date date = new Date();
//                String currentTime = timeFormatter.format(date);
//
//                // currentUser send
//                if (msg.getString("from").equals(builder.getPersonalUser().getName())) {
//                    channelName = msg.getString("to");
//                    message = new Message().setMessage(msg.getString("message")).
//                            setFrom(msg.getString("from")).
//                            setTimestamp(msg.getLong("timestamp")).setCurrentTime(currentTime);
//                    builder.getPrivateMessageController().clearMessageField();
//                } else { // currentUser received
//                    channelName = msg.getString("from");
//                    message = new Message().setMessage(msg.getString("message")).
//                            setFrom(msg.getString("from")).
//                            setTimestamp(msg.getLong("timestamp")).setCurrentTime(currentTime);
//                }
//                for (Channel channel : builder.getPersonalUser().getPrivateChat()) {
//                    if (channel.getName().equals(channelName)) {
//                        channel.withMessage(message);
//                        if (builder.getSelectedPrivateChat() == null || channel != builder.getSelectedPrivateChat()) {
//                            channel.setUnreadMessagesCounter(channel.getUnreadMessagesCounter() + 1);
//                        }
//                        privateChatsController.updatePrivateChatsRV();
//                        builder.getPrivateMessageController().notifyOnMessageAdded();
//                        newChat = false;
//                        break;
//                    }
//                }
//                if (newChat) {
//                    String userId = "";
//                    for (User user : builder.getPersonalUser().getUser()) {
//                        if (user.getName().equals(channelName)) {
//                            userId = user.getId();
//                        }
//                    }
//                    Channel channel = new Channel().setId(userId).setName(channelName).withMessage(message).setUnreadMessagesCounter(1);
//                    builder.getPersonalUser().withPrivateChat(channel);
//                    privateChatsController.updatePrivateChatsRV();
//                    builder.getPrivateMessageController().notifyOnMessageAdded();
//                }
//                if (builder.getPrivateMessageController() != null) {
//                    //privateMessageController.printMessage(message); //PRINT MESSAGE
//                }
//            }
//            if (msg.has("action") && msg.getString("action").equals("info")) {
//                String errorTitle;
//                String serverMessage = msg.getJSONObject("data").getString("message");
//                if (serverMessage.equals("This is not your username.")) {
//                    System.out.print("This is not your username.");
//                    Toast.makeText(builder.getMainActivity(), "This is not your username.", Toast.LENGTH_LONG).show();
//                }
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void receivedServerMessage(JSONObject msg) throws JSONException {
        String channelId = msg.getString("channel");
        String from = msg.getString("from");
        String id = msg.getString("id");
        String messageText = msg.getString("text");
        long timestamp = msg.getLong("timestamp");

        Date date = new Date();
        String currentTime = timeFormatter.format(date);

        Message message = new Message()
                .setId(id)
                .setMessage(messageText)
                .setFrom(from)
                .setTimestamp(timestamp)
                .setCurrentTime(currentTime);

        if (from.equals(builder.getPersonalUser().getName())) {
            // currentUser send
            for (Categories category : server.getCategories()) {
                for (ServerChannel channel : category.getChannel()) {
                    if (channel.getId().equals(channelId)) {
                        channel.withMessage(message);
                        break;
                    }
                }
            }
            builder.getServerMessageController().clearMessageField();
        } else {
            // currentUser received
            for (Categories category : server.getCategories()) {
                for (ServerChannel channel : category.getChannel()) {
                    if (channel.getId().equals(channelId)) {
                        channel.withMessage(message);
                        handleMessageNotifications(channel);
                        if (builder.getCurrentServer() == server) {
                            builder.getChannelAdapterMap().get(category.getId()).notifyItemChanged(category.getChannel().indexOf(channel));
                        }
                        break;
                    }
                }
            }
        }

        if (builder.getCurrentServer() == server && server.getCurrentServerChannel() != null && server.getCurrentServerChannel().getId().equals(channelId)) {
            builder.getServerMessageController().notifyOnMessageAdded();
        }
    }

    private void handleMessageNotifications(ServerChannel channel) {
//        if (!builder.isDoNotDisturb() && (serverViewController.getCurrentChannel() == null || channel != serverViewController.getCurrentChannel())) {
//            if (builder.isPlaySound()) {
//                builder.playSound();
//            }
//            if (builder.isShowNotifications()) {
//                channel.setUnreadMessagesCounter(channel.getUnreadMessagesCounter() + 1);
//            }
//        }

        // count new message when chat is not open
        if (server.getCurrentServerChannel() == null || server.getCurrentServerChannel() != channel) {
            channel.setUnreadMessagesCounter(channel.getUnreadMessagesCounter() + 1);
        }
    }
}

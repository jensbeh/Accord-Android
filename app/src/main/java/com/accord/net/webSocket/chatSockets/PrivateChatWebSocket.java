package com.accord.net.webSocket.chatSockets;


import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.widget.Toast;

import com.accord.ModelBuilder;
import com.accord.model.Channel;
import com.accord.model.Message;
import com.accord.model.User;
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


public class PrivateChatWebSocket extends Endpoint {
    private ModelBuilder builder;
    private SimpleDateFormat timeFormatter;

    private Session session;
    private Timer noopTimer;
    public static final String COM_NOOP = "noop";

    @SuppressLint("SimpleDateFormat")
    public PrivateChatWebSocket(ModelBuilder modelBuilder, URI endpoint) {
        this.builder = modelBuilder;
        this.noopTimer = new Timer();
        timeFormatter = new SimpleDateFormat("HH:mm");

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

            if (msg.has("channel") && msg.getString("channel").equals("private")) {
                Message message;
                String channelName;
                Boolean newChat = true;

                Date date = new Date();
                String currentTime = timeFormatter.format(date);

                // currentUser send
                if (msg.getString("from").equals(builder.getPersonalUser().getName())) {
                    channelName = msg.getString("to");
                    message = new Message().setMessage(msg.getString("message")).
                            setFrom(msg.getString("from")).
                            setTimestamp(msg.getInt("timestamp")).setCurrentTime(currentTime);
                    builder.getPrivateMessageController().clearMessageField();
                } else { // currentUser received
                    channelName = msg.getString("from");
                    message = new Message().setMessage(msg.getString("message")).
                            setFrom(msg.getString("from")).
                            setTimestamp(msg.getInt("timestamp")).setCurrentTime(currentTime);
                }
                for (Channel channel : builder.getPersonalUser().getPrivateChat()) {
                    if (channel.getName().equals(channelName)) {
                        channel.withMessage(message);
                        if (builder.getSelectedPrivateChat() == null || channel != builder.getSelectedPrivateChat()) {
                            channel.setUnreadMessagesCounter(channel.getUnreadMessagesCounter() + 1);
                        }
                        builder.getMainActivity().updatePrivateChatRV();
                        builder.getPrivateMessageController().updatePrivateMessagesFragment();
                        newChat = false;
                        break;
                    }
                }
                if (newChat) {
                    String userId = "";
                    for (User user : builder.getPersonalUser().getUser()) {
                        if (user.getName().equals(channelName)) {
                            userId = user.getId();
                        }
                    }
                    Channel channel = new Channel().setId(userId).setName(channelName).withMessage(message).setUnreadMessagesCounter(1);
                    builder.getPersonalUser().withPrivateChat(channel);
                    builder.getMainActivity().updatePrivateChatRV();
                    builder.getPrivateMessageController().updatePrivateMessagesFragment();
                }
                if (builder.getPrivateMessageController() != null) {
                    //privateMessageController.printMessage(message); //PRINT MESSAGE
                }
            }
            if (msg.has("action") && msg.getString("action").equals("info")) {
                String errorTitle;
                String serverMessage = msg.getJSONObject("data").getString("message");
                if (serverMessage.equals("This is not your username.")) {
                    System.out.print("This is not your username.");
                    Toast.makeText(builder.getMainActivity(), "This is not your username.", Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

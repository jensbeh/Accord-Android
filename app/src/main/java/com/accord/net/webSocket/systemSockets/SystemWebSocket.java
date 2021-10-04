package com.accord.net.webSocket.systemSockets;


import android.os.StrictMode;
import android.widget.Toast;

import com.accord.ModelBuilder;
import com.accord.model.User;
import com.accord.net.webSocket.CustomWebSocketConfigurator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;


public class SystemWebSocket extends Endpoint {
    private ModelBuilder builder;

    private Session session;
    private Timer noopTimer;
    public static final String COM_NOOP = "noop";

    public SystemWebSocket(ModelBuilder modelBuilder, URI endpoint) {
        this.builder = modelBuilder;
        this.noopTimer = new Timer();

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
            System.out.println("msg: " + msg);
            String userAction = msg.getString("action");
            JSONObject jsonData = msg.getJSONObject("data");
            String userName = jsonData.getString("name");
            String userId = jsonData.getString("id");

            if (userAction.equals("userJoined")) {
                builder.buildUser(userName, userId, "");
            }
            if (userAction.equals("userLeft")) {
                if (userName.equals(builder.getPersonalUser().getName())) {
                    builder.getMainActivity().showLoginActivity();
                }

                List<User> userList = builder.getPersonalUser().getUser();
                User removeUser = builder.buildUser(userName, userId, "");
                if (userList.contains(removeUser)) {
                    builder.getPersonalUser().withoutUser(removeUser);
                }
            }
            //modelBuilder.getPersonalUser().getUser().sort(new SortUser());
            builder.getOnlineUserController().updateOnlineUsersRV();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
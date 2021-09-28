package com.accord.net.webSocket;


import android.os.StrictMode;

import com.accord.ModelBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;


public class WebSocketClient extends Endpoint {
    private Session session;
    private Timer noopTimer;
    public static final String COM_NOOP = "noop";

    private WSCallback callback;

    public WebSocketClient(ModelBuilder modelBuilder, URI endpoint, WSCallback callback) {
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
            this.callback = callback;
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
                System.out.println("##### NOOP MESSAGE #####");
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
        this.callback.onClose(session, closeReason);
    }

    private void onMessage(String message) {
        try {
            // Process Message
            JSONObject jsonObject = new JSONObject(message);
            // Use callback to handle it
            this.callback.handleMessage(jsonObject);
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
}

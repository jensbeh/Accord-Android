package com.accord.net.webSocket.systemSockets;


import android.os.StrictMode;
import android.widget.Toast;

import com.accord.ModelBuilder;
import com.accord.model.Categories;
import com.accord.model.Server;
import com.accord.model.ServerChannel;
import com.accord.net.webSocket.CustomWebSocketConfigurator;

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


public class ServerSystemWebSocket extends Endpoint {
    private ModelBuilder builder;
    private Server server;

    private Session session;
    private Timer noopTimer;
    public static final String COM_NOOP = "noop";

    public ServerSystemWebSocket(ModelBuilder modelBuilder, Server server, URI endpoint) {
        this.builder = modelBuilder;
        this.server = server;
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

    public void handleMessage(JSONObject msg) throws JSONException {
        String userAction = msg.getString("action");
        JSONObject jsonData = msg.getJSONObject("data");
        String userName = "";
        String userId = "";
        if (!userAction.equals("audioJoined") && !userAction.equals("audioLeft") && !userAction.equals("messageUpdated") && !userAction.equals("messageDeleted")) {
            userName = jsonData.getString("name");
            userId = jsonData.getString("id");
        }
//        if (userAction.equals("categoryCreated")) {
//            createCategory(jsonData);
//        }
//        if (userAction.equals("categoryDeleted")) {
//            deleteCategory(jsonData);
//        }
//        if (userAction.equals("categoryUpdated")) {
//            updateCategory(jsonData);
//        }
//
        if (userAction.equals("channelCreated")) {
            createChannel(jsonData);
        }
//        if (userAction.equals("channelDeleted")) {
//            deleteChannel(jsonData);
//        }
//        if (userAction.equals("channelUpdated")) {
//            updateChannel(jsonData);
//        }
//
//        if (userAction.equals("userArrived")) {
//            userArrived(jsonData);
//        }
//        if (userAction.equals("userExited")) {
//            userExited(jsonData);
//        }
//
        if (userAction.equals("userJoined")) {
            builder.buildServerUser(server, userName, userId, true, "");
            builder.getServerMembersController().updateOnlineMembersRV();
            builder.getServerMembersController().updateOfflineMembersRV();
            builder.getServerMembersController().updateOnlineOfflineMemberCount();
        }
        if (userAction.equals("userLeft")) {
            builder.buildServerUser(server, userName, userId, false, "");
            builder.getServerMembersController().updateOnlineMembersRV();
            builder.getServerMembersController().updateOfflineMembersRV();
            builder.getServerMembersController().updateOnlineOfflineMemberCount();
        }
//
//        if (userAction.equals("serverDeleted")) {
//            deleteServer();
//        }
//        if (userAction.equals("serverUpdated")) {
//            updateServer(userName);
//        }
//
//        // audioChannel
//        if (userAction.equals("audioJoined")) {
//            joinAudio(jsonData);
//        }
//        if (userAction.equals("audioLeft")) {
//            leaveAudio(jsonData);
//        }
//
//        if (userAction.equals("messageUpdated")) {
//            updateMessage(jsonData);
//        }
//
//        if (userAction.equals("messageDeleted")) {
//            deleteMessage(jsonData);
//        }
//
//        if (builder.getCurrentServer() == serverViewController.getServer()) {
//            serverViewController.showOnlineOfflineUsers();
//        }
    }

    /**
     * adds the new channel to category for the user
     *
     * @param jsonData the message data
     */
    private void createChannel(JSONObject jsonData) throws JSONException {
        for (Server server : builder.getPersonalUser().getServer()) {
            if (findCategoryForNewChannel(server, jsonData)) {
                break;
            }
        }
    }

    private boolean findCategoryForNewChannel(Server server, JSONObject jsonData) throws JSONException {
        String channelId = jsonData.getString("id");
        String channelName = jsonData.getString("name");
        String channelType = jsonData.getString("type");
        boolean channelPrivileged = jsonData.getBoolean("privileged");
        String categoryId = jsonData.getString("category");

        for (Categories category : server.getCategories()) {
            if (category.getId().equals(categoryId)) {
                ServerChannel newChannel = new ServerChannel();
                newChannel.setId(channelId);
                newChannel.setType(channelType);
                newChannel.setName(channelName);
                newChannel.setPrivilege(channelPrivileged);
                category.withChannel(newChannel);

                // update category with the new channel
                builder.getServerCategoriesAdapter().notifyItemChanged(server.getCategories().indexOf(category));

                return true;
            }
        }
        return false;
    }
}

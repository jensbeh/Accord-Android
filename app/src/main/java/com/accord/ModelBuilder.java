package com.accord;

import com.accord.model.Channel;
import com.accord.model.CurrentUser;
import com.accord.model.Server;
import com.accord.model.User;
import com.accord.net.webSocket.chatSockets.PrivateChatWebSocket;
import com.accord.net.webSocket.systemSockets.SystemWebSocket;
import com.accord.ui.home.HomeFragment;
import com.accord.ui.privateChat.PrivateMessageFragment;
import com.accord.ui.server.ServerFragment;

public class ModelBuilder {

    private Server currentServer;
    private CurrentUser personalUser;

    private SystemWebSocket systemWebSocket;

    private Channel selectedPrivateChat;
    private PrivateChatWebSocket privateChatWebSocket;
    private MainActivity.State state;

    private MainActivity mainActivity;
    private HomeFragment homeController;
    private PrivateMessageFragment privateMessageController;
    private ServerFragment serverController;

    public void buildPersonalUser(String name, String userKey) {
        personalUser = new CurrentUser().setName(name).setUserKey(userKey);
    }

    public CurrentUser getPersonalUser() {
        return personalUser;
    }

    public User buildUser(String name, String id) {
        for (User user : personalUser.getUser()) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        User newUser = new User().setName(name).setId(id).setStatus(true);
        personalUser.withUser(newUser);
        return newUser;
    }

    public Server buildServer(String name, String id) {
        for (Server server : personalUser.getServer()) {
            if (server.getId().equals(id)) {
                return server;
            }
        }
        Server newServer = new Server().setName(name).setId(id);
        personalUser.withServer(newServer);
        return newServer;
    }

    public void setCurrentServer(Server currentServer) {
        this.currentServer = currentServer;
    }

    public Server getCurrentServer() {
        return currentServer;
    }


    public void setSelectedPrivateChat(Channel selectedPrivateChat) {
        this.selectedPrivateChat = selectedPrivateChat;
    }

    public Channel getSelectedPrivateChat() {
        return this.selectedPrivateChat;
    }

    public void setState(MainActivity.State state) {
        this.state = state;
    }

    public MainActivity.State getState() {
        return state;
    }

    //////////////////////////////////
    // WebSockets
    //////////////////////////////////
    public void setSystemWebSocket(SystemWebSocket systemWebSocket) {
        this.systemWebSocket = systemWebSocket;
    }

    public PrivateChatWebSocket getPrivateChatWebSocketClient() {
        return privateChatWebSocket;
    }

    public void setPrivateChatWebSocket(PrivateChatWebSocket privateChatWebSocket) {
        this.privateChatWebSocket = privateChatWebSocket;
    }

    //////////////////////////////////
    // Activities & FragmentController
    //////////////////////////////////
    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setHomeController(HomeFragment homeController) {
        this.homeController = homeController;
    }
    public HomeFragment getHomeController() {
        return homeController;
    }

    public void setPrivateMessageController(PrivateMessageFragment privateMessageController) {
        this.privateMessageController = privateMessageController;
    }
    public PrivateMessageFragment getPrivateMessageController() {
        return privateMessageController;
    }

    public void setServerController(ServerFragment serverController) {
        this.serverController = serverController;
    }
    public ServerFragment getServerController() {
        return serverController;
    }

    /*
    private Server currentServer;
    private CurrentUser personalUser;
    private WebSocketClient SERVER_USER;
    private WebSocketClient USER_CLIENT;
    private WebSocketClient privateChatWebSocketCLient;
    /////////////////////////////////////////
    //  Setter
    /////////////////////////////////////////




    public User buildServerUser(String name, String id, Boolean status) {
        for (User user : currentServer.getUser()) {
            if (user.getId().equals(id)) {
                if (user.isStatus() == status) {
                    return user;
                } else {
                    currentServer.withoutUser(user);
                    User updatedUser = new User().setName(name).setId(id).setStatus(status);
                    currentServer.withUser(updatedUser);
                    return updatedUser;
                }
            }
        }
        User newUser = new User().setName(name).setId(id).setStatus(status);
        currentServer.withUser(newUser);
        return newUser;
    }

    public Server buildServer(String name, String id) {
        for (Server server : personalUser.getServer()) {
            if (server.getId().equals(id)) {
                return server;
            }
        }
        Server newServer = new Server().setName(name).setId(id);
        personalUser.withServer(newServer);
        return newServer;
    }


    /////////////////////////////////////////
    //  Getter
    /////////////////////////////////////////

    public List<Server> getServers() {
        return this.personalUser.getServer() != null ? Collections.unmodifiableList(this.personalUser.getServer()) : Collections.emptyList();
    }


    public Server getCurrentServer() {
        return currentServer;
    }


    public WebSocketClient getSERVER_USER() {
        return SERVER_USER;
    }

    public void setSERVER_USER(WebSocketClient SERVER_USER) {
        this.SERVER_USER = SERVER_USER;
    }

    public WebSocketClient getUSER_CLIENT() {
        return USER_CLIENT;
    }

    public void setUSER_CLIENT(WebSocketClient USER_CLIENT) {
        this.USER_CLIENT = USER_CLIENT;
    }

    public WebSocketClient getPrivateChatWebSocketCLient() {
        return privateChatWebSocketCLient;
    }

    public void setPrivateChatWebSocketCLient(WebSocketClient privateChatWebSocketCLient) {
        this.privateChatWebSocketCLient = privateChatWebSocketCLient;
    }*/
}
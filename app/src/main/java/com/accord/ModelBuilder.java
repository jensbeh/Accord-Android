package com.accord;

import com.accord.adapter.leftDrawer.itemContainer.ServerCategoriesRecyclerViewAdapter;
import com.accord.adapter.leftDrawer.itemContainer.ServerChannelsRecyclerViewAdapter;
import com.accord.model.Channel;
import com.accord.model.CurrentUser;
import com.accord.model.Server;
import com.accord.model.User;
import com.accord.net.rest.RestClient;
import com.accord.net.webSocket.chatSockets.PrivateChatWebSocket;
import com.accord.net.webSocket.chatSockets.ServerChatWebSocket;
import com.accord.net.webSocket.systemSockets.ServerSystemWebSocket;
import com.accord.net.webSocket.systemSockets.SystemWebSocket;
import com.accord.ui.chatMessages.PrivateMessagesFragment;
import com.accord.ui.chatMessages.ServerMessagesFragment;
import com.accord.ui.home.HomeFragment;
import com.accord.ui.home.OnlineUsersFragment;
import com.accord.ui.home.PrivateChatsFragment;
import com.accord.ui.server.ServerFragment;
import com.accord.ui.server.ServerMembersFragment;

import java.util.HashMap;
import java.util.Map;

public class ModelBuilder {
    private Map<String, ServerChannelsRecyclerViewAdapter> channelAdapterMap = new HashMap<>();
    private ServerCategoriesRecyclerViewAdapter serverCategoriesAdapter;
    private RestClient restClient;

    private Server currentServer;
    private CurrentUser personalUser;

    private SystemWebSocket systemWebSocket;
    private PrivateChatWebSocket privateChatWebSocket;
    private Map<String, ServerSystemWebSocket> serverSystemWebSocketsMap = new HashMap<>();

    private Channel selectedPrivateChat;
    private MainActivity.State state;

    private MainActivity mainActivity;
    private HomeFragment homeController;
    private PrivateMessagesFragment privateMessageController;
    private ServerFragment serverController;
    private ServerMessagesFragment serverMessageController;
    private Map<String, ServerChatWebSocket> serverChatWebSocketsMap = new HashMap<>();
    private PrivateChatsFragment privateChatsController;
    private OnlineUsersFragment onlineUserController;
    private ServerMembersFragment serverMembersController;

    public void buildPersonalUser(String name, String password, String userKey) {
        personalUser = new CurrentUser().setName(name).setUserKey(userKey).setPassword(password);
    }

    public CurrentUser getPersonalUser() {
        return personalUser;
    }

    public User buildUser(String name, String id, String description) {
        for (User user : personalUser.getUser()) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        User newUser = new User().setName(name).setId(id).setStatus(true).setDescription(description).setUserVolume(100.0);
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

    public User buildServerUser(Server server, String name, String id, Boolean status, String description) {
        for (User user : server.getUser()) {
            if (user.getId().equals(id)) {
                if (user.isStatus() == status) {
                    return user;
                } else {
                    server.withoutUser(user);
                    User updatedUser = new User().setName(name).setId(id).setStatus(status).setDescription(description);
                    server.withUser(updatedUser);
                    return updatedUser;
                }
            }
        }
        User newUser = new User().setName(name).setId(id).setStatus(status).setDescription(description);
        server.withUser(newUser);
        return newUser;
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

    public Map<String, ServerChannelsRecyclerViewAdapter> getChannelAdapterMap() {
        return channelAdapterMap;
    }

    public void setChannelAdapterMap(Map<String, ServerChannelsRecyclerViewAdapter> channelAdapterMap) {
        this.channelAdapterMap = channelAdapterMap;
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

    public void addServerSystemWebSocket(String serverId, ServerSystemWebSocket serverSystemWebSocket) {
        this.serverSystemWebSocketsMap.put(serverId, serverSystemWebSocket);
    }

    public Map<String, ServerSystemWebSocket> getServerSystemWebSocketsMap() {
        return serverSystemWebSocketsMap;
    }

    public void removeServerSystemWebSocket(String serverId) {
        this.serverSystemWebSocketsMap.remove(serverId);
    }

    public void addServerChatWebSocket(String serverId, ServerChatWebSocket serverChatWebSocket) {
        this.serverChatWebSocketsMap.put(serverId, serverChatWebSocket);
    }

    public Map<String, ServerChatWebSocket> getServerChatWebSocketsMap() {
        return serverChatWebSocketsMap;
    }

    public void removeServerChatWebSocket(String serverId) {
        this.serverChatWebSocketsMap.remove(serverId);
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

    public void setOnlineUserController(OnlineUsersFragment onlineUserController) {
        this.onlineUserController = onlineUserController;
    }

    public OnlineUsersFragment getOnlineUserController() {
        return onlineUserController;
    }

    public void setPrivateMessageController(PrivateMessagesFragment privateMessageController) {
        this.privateMessageController = privateMessageController;
    }

    public PrivateMessagesFragment getPrivateMessageController() {
        return privateMessageController;
    }

    public void setServerController(ServerFragment serverController) {
        this.serverController = serverController;
    }

    public ServerFragment getServerController() {
        return serverController;
    }

    public void setRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public RestClient getRestClient() {
        return restClient;
    }

    public void setServerCategoriesAdapter(ServerCategoriesRecyclerViewAdapter serverCategoriesAdapter) {
        this.serverCategoriesAdapter = serverCategoriesAdapter;
    }

    public ServerCategoriesRecyclerViewAdapter getServerCategoriesAdapter() {
        return serverCategoriesAdapter;
    }

    public void setServerMessageController(ServerMessagesFragment serverMessageController) {
        this.serverMessageController = serverMessageController;
    }

    public ServerMessagesFragment getServerMessageController() {
        return serverMessageController;
    }

    public void setPrivateChatsController(PrivateChatsFragment privateChatsController) {
        this.privateChatsController = privateChatsController;
    }

    public PrivateChatsFragment getPrivateChatsController() {
        return privateChatsController;
    }

    public void setServerMembersController(ServerMembersFragment serverMembersController) {
        this.serverMembersController = serverMembersController;
    }

    public ServerMembersFragment getServerMembersController() {
        return serverMembersController;
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
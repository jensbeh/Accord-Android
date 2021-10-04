package com.accord;

import static com.accord.util.Constants.CHAT_WEBSOCKET_PATH;
import static com.accord.util.Constants.SERVER_SYSTEM_WEBSOCKET_PATH;
import static com.accord.util.Constants.SERVER_WEBSOCKET_PATH;
import static com.accord.util.Constants.SYSTEM_WEBSOCKET_PATH;
import static com.accord.util.Constants.WS_SERVER_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accord.adapter.leftDrawer.ServerRecyclerViewAdapter;
import com.accord.adapter.rightDrawer.OnlineUserRecyclerViewAdapter;
import com.accord.bottomSheets.BottomSheetCreateServer;
import com.accord.model.Categories;
import com.accord.model.Channel;
import com.accord.model.Message;
import com.accord.model.Server;
import com.accord.model.ServerChannel;
import com.accord.model.User;
import com.accord.net.rest.RestClient;
import com.accord.net.rest.responses.ResponseWithJsonList;
import com.accord.net.rest.responses.ResponseWithJsonObject;
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
import com.accord.ui.server.ServerItemsFragment;
import com.accord.ui.server.ServerMembersFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;

// upgraded gradle from 4.2.2 to 7.0.2
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static ModelBuilder builder;
    private RestClient restClient;
    private DrawerLayout drawer;
    private NavigationView navigationViewLeft;
    private HomeFragment homeController;
    private OnlineUsersFragment onlineUserController;
    private PrivateChatsFragment privateChatsController;
    private ServerItemsFragment serverItemsController;
    private PrivateMessagesFragment privateMessageController;
    private ServerMessagesFragment serverMessageController;
    private ServerFragment serverController;
    private ServerMembersFragment serverMembersController;

    public enum State {
        HomeView,
        PrivateChatView,
        ServerView
    }

    private TextView text_username;
    private TextView text_userKey;
    private Button button_logout;
    private CardView button_Home;
    private CardView button_addServer;
    private NavigationView navigationViewRight;
    private RecyclerView rv_server;
    private OnlineUserRecyclerViewAdapter onlineUserRecyclerViewAdapter;
    private ServerRecyclerViewAdapter serverRecyclerViewAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String KEY_PREFS = "privateChats";

    // notifications
    private NotificationManagerCompat notificationManagerCompat;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get personalUser information from loginActivity
        Bundle bundle = getIntent().getExtras();
        String username = bundle.getString("username");
        String password = bundle.getString("password");
        String userKey = bundle.getString("userKey");

        // Create ModelBuilder
        builder = new ModelBuilder();
        builder.buildPersonalUser(username, password, userKey);
        builder.setState(State.HomeView);
        builder.setMainActivity(this);

        // set notification manager to builder
        

        // create RestClient
        restClient = new RestClient();
        restClient.setup();
        builder.setRestClient(restClient);

        // get drawer
        drawer = findViewById(R.id.drawer_layout);
        navigationViewLeft = findViewById(R.id.nav_view_left);
        navigationViewRight = findViewById(R.id.nav_view_right);
        navigationViewLeft.setNavigationItemSelectedListener(this);

        // Setup navigation and screen when start
        homeController = new HomeFragment(builder);
        onlineUserController = new OnlineUsersFragment(builder);
        privateChatsController = new PrivateChatsFragment(builder);
        privateMessageController = new PrivateMessagesFragment(builder);
        serverMessageController = new ServerMessagesFragment(builder);
        serverController = new ServerFragment(builder);
        serverItemsController = new ServerItemsFragment(builder);
        serverMembersController = new ServerMembersFragment(builder);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                    homeController).commit();
            navigationViewLeft.setCheckedItem(R.id.nav_Home);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_items,
                    privateChatsController).commit();
        }

        ////////////////////////////////////////////////////

        rv_server = navigationViewLeft.findViewById(R.id.rv_server);
        button_logout = navigationViewLeft.findViewById(R.id.button_logout);
        button_Home = navigationViewLeft.findViewById(R.id.button_Home);
        button_addServer = navigationViewLeft.findViewById(R.id.button_add);
        text_username = navigationViewLeft.findViewById(R.id.text_username);
        text_userKey = navigationViewLeft.findViewById(R.id.text_userKey);

        text_username.setText(builder.getPersonalUser().getName());
        text_userKey.setText(builder.getPersonalUser().getUserKey());

        builder.setHomeController(homeController);
        builder.setOnlineUserController(onlineUserController);
        builder.setPrivateChatsController(privateChatsController);
        builder.setPrivateMessageController(privateMessageController);
        builder.setServerMessageController(serverMessageController);
        builder.setServerController(serverController);
        builder.setServerMembersController(serverMembersController);

        button_logout.setOnClickListener(this::onLogoutButtonClick);
        button_Home.setOnClickListener(this::onHomeButtonClick);
        button_addServer.setOnClickListener(this::onAddServerButtonClick);

        sharedPreferences = this.getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // load all sharedPreferences
        loadData();

        // setup RecyclerViews with listener
        showOnlineUsers();
        setupServersRecyclerView();

        // setup webSockets for system messages and private chats
        setupWebSockets();

        // Get online users & servers and view them
        loadOnlineUsers();
        loadServer(() -> {
            System.out.println("All server loaded!");
            // after loading all servers create the webSockets
            setupServerWebSockets();
        });

        // add navigationBar listener
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                // close Keyboard
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            // When closed a navigation view unlock the other
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                if (drawer.getDrawerLockMode(findViewById(R.id.nav_view_left)) == DrawerLayout.LOCK_MODE_LOCKED_CLOSED) {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, findViewById(R.id.nav_view_left));

                } else if (drawer.getDrawerLockMode(findViewById(R.id.nav_view_right)) == DrawerLayout.LOCK_MODE_LOCKED_CLOSED) {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, findViewById(R.id.nav_view_right));
                }
            }

            // When opened a navigation view lock the other
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                NavigationView nav_view = drawerView.findViewById(R.id.nav_view_left);
                NavigationView nav_view_right = drawerView.findViewById(R.id.nav_view_right);

                if (nav_view == null) {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.nav_view_left));
                } else if (nav_view_right == null) {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.nav_view_right));
                }
            }
        };
        drawer.addDrawerListener(mDrawerToggle);
    }

    public interface FullyLoadedCallback {
        void onSuccess();
    }

    /**
     * Update the builder and get the ServerUser as well as the categories. Also sets their online and offline Status.
     */
    public interface ServerUserCallback {
        void onSuccess(String status);
    }

    private void loadServer(FullyLoadedCallback fullyLoadedCallback) {
        restClient.doGetServer(builder.getPersonalUser().getUserKey(), new RestClient.ResponseCallbackWithList() {
            private int loadedServers = 0;

            @Override
            public void onSuccess(String status, ArrayList<ResponseWithJsonList.Data> dataArrayList) {
                for (ResponseWithJsonList.Data data : dataArrayList) {
                    String serverId = data.getId();
                    String serverName = data.getName();

                    Server loadedServer = builder.buildServer(serverName, serverId);
                    loadServerUsers(loadedServer, new ServerUserCallback() {
                        @Override
                        public void onSuccess(String status) {
                            loadCategories(loadedServer, new CategoriesLoadedCallback() {
                                @Override
                                public void onSuccess(String status) {
                                    // categories ready
                                    loadedServers++;
                                    if (loadedServers == dataArrayList.size()) {
                                        loadedServers = 0;
//                                        categoriesLoadedCallback.onSuccess(status1);
                                        fullyLoadedCallback.onSuccess();
                                    }
                                }
                            });
                        }
                    });
                }
                updateServerRV();
            }

            @Override
            public void onFailed(Throwable error) {
                System.out.print("Error: " + error.getMessage());
            }
        });
    }

    public void loadServerUsers(Server server, ServerUserCallback serverUserCallback) {
        restClient.doGetServerUsers(server.getId(), builder.getPersonalUser().getUserKey(), new RestClient.ResponseCallbackWithObject() {

            @Override
            public void onSuccess(String status, ResponseWithJsonObject.Data data) {
                System.out.print(status);
                System.out.print(data);

                server.setOwner(data.getOwner());
                for (ResponseWithJsonObject.Member member : data.getMembers()) {
                    String id = member.getId();
                    String description = member.getDescription();
                    String name = member.getName();
                    boolean online = member.isOnline();
                    builder.buildServerUser(server, name, id, online, description);
                }

                for (int i = 0; i < 5; i++) {
                    builder.buildServerUser(server, "testUser" + i, "id" + i, true, "description");
                }
                for (int i = 5; i < 15; i++) {
                    builder.buildServerUser(server, "testUser" + i, "id" + i, false, "description");
                }

                serverUserCallback.onSuccess(status);
            }

            @Override
            public void onFailed(Throwable error) {
                System.out.print("Error: " + error.getMessage());
            }
        });
    }

    /**
     * Callback, when all category information are loaded
     */
    public interface CategoriesLoadedCallback {
        void onSuccess(String status);
    }

    public void loadCategories(Server server, CategoriesLoadedCallback categoriesLoadedCallback) {
        restClient.doGetCategories(server.getId(), builder.getPersonalUser().getUserKey(), new RestClient.ResponseCallbackWithList() {

            private int loadedCategories = 0;

            @Override
            public void onSuccess(String status, ArrayList<ResponseWithJsonList.Data> dataArrayList) {
                System.out.print(status);

                for (ResponseWithJsonList.Data data : dataArrayList) {
                    String categoryName = data.getName();
                    String categoryId = data.getId();

                    Categories loadedCategory = new Categories().setId(categoryId).setName(categoryName);
                    server.withCategories(loadedCategory);

                    loadChannel(server, loadedCategory, status1 -> {
                        loadedCategories++;
                        if (loadedCategories == dataArrayList.size()) {
                            loadedCategories = 0;
                            categoriesLoadedCallback.onSuccess(status1);
                        }
                    });
                }
                updateServerRV();
            }

            @Override
            public void onFailed(Throwable error) {
                System.out.print("Error: " + error.getMessage());
            }
        });
    }


    /**
     * Callback, when all channel information are loaded
     */
    public interface ChannelLoadedCallback {
        void onSuccess(String status);
    }

    private void loadChannel(Server server, Categories category, ChannelLoadedCallback channelLoadedCallback) {
        restClient.doGetChannels(server.getId(), category.getId(), builder.getPersonalUser().getUserKey(), new RestClient.ResponseCallbackWithList() {

            private int loadedChannel = 0;

            @Override
            public void onSuccess(String status, ArrayList<ResponseWithJsonList.Data> dataArrayList) {
                System.out.print(status);
                for (ResponseWithJsonList.Data data : dataArrayList) {
                    String channelName = data.getName();
                    String channelId = data.getId();
                    String channelType = data.getType();
                    boolean channelIsPrivileged = data.isPrivileged();

                    ServerChannel serverChannel = new ServerChannel();
                    serverChannel.setId(channelId);
                    serverChannel.setName(channelName);
                    serverChannel.setType(channelType);
                    serverChannel.setPrivilege(channelIsPrivileged);
                    category.withChannel(serverChannel);

                    for (ResponseWithJsonList.AudioMember audioMember : data.getAudioMembers()) {
                        for (User user : server.getUser()) {
                            if (user.getId().equals(audioMember.getId())) {
                                serverChannel.withAudioMember(user);
                            }
                        }
                    }

                    for (ResponseWithJsonList.Member member : data.getMembers()) {
                        for (User user : server.getUser()) {
                            if (user.getId().equals(member.getId())) {
                                serverChannel.withPrivilegedUsers(user);
                            }
                        }
                    }
                    if (serverChannel.getType().equals("text")) {
                        loadMessages(server, category, serverChannel, status1 -> {
                            loadedChannel++;
                            if (loadedChannel == dataArrayList.size()) {
                                loadedChannel = 0;
                                channelLoadedCallback.onSuccess(status1);
                            }
                        });
                    } else {
                        loadedChannel++;
                        if (loadedChannel == dataArrayList.size()) {
                            loadedChannel = 0;
                            channelLoadedCallback.onSuccess("success");
                        }
                    }
                }
            }

            @Override
            public void onFailed(Throwable error) {
                System.out.print("Error: " + error.getMessage());
            }
        });
    }

    /**
     * Callback, when all message information are loaded
     */
    public interface MessagesLoadedCallback {
        void onSuccess(String status);

    }

    private void loadMessages(Server server, Categories category, ServerChannel serverChannel, MessagesLoadedCallback messagesLoadedCallback) {
        restClient.doGetMessages(new Date().getTime(), server.getId(), category.getId(), serverChannel.getId(), builder.getPersonalUser().getUserKey(), new RestClient.ResponseCallbackWithList() {

            @Override
            public void onSuccess(String status, ArrayList<ResponseWithJsonList.Data> dataArrayList) {
                System.out.print(status);

                for (ResponseWithJsonList.Data data : dataArrayList) {
                    String from = data.getFrom();
//                    double timestampDouble = (double) messageMap.get("timestamp");
//                    long timestamp = (long) timestampDouble;
                    long timestamp = data.getTimestamp();
                    String text = data.getText();
                    String id = data.getId();

                    Message loadedMessage = new Message().setMessage(text).setFrom(from).setTimestamp(timestamp).setId(id);
                    serverChannel.withMessage(loadedMessage);
                }
                messagesLoadedCallback.onSuccess(status);
            }

            @Override
            public void onFailed(Throwable error) {
                System.out.print("Error: " + error.getMessage());
            }
        });
    }


    /**
     * setup the system webSocket & privateChat webSocket
     */
    private void setupWebSockets() {
        try {
            SystemWebSocket systemWebSocket = new SystemWebSocket(builder, new URI(WS_SERVER_URL + SYSTEM_WEBSOCKET_PATH));
            builder.setSystemWebSocket(systemWebSocket);

            PrivateChatWebSocket privateChatWebSocket = new PrivateChatWebSocket(builder, privateChatsController, URI.create(WS_SERVER_URL + CHAT_WEBSOCKET_PATH + builder.getPersonalUser().getName().replace(" ", "+")));
            builder.setPrivateChatWebSocket(privateChatWebSocket);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void setupServerWebSockets() {
        for (Server server : builder.getPersonalUser().getServer()) {
            ServerSystemWebSocket serverSystemWebSocket = new ServerSystemWebSocket(builder, server, URI.create(WS_SERVER_URL + SERVER_SYSTEM_WEBSOCKET_PATH + server.getId()));
            builder.addServerSystemWebSocket(server.getId(), serverSystemWebSocket);

            ServerChatWebSocket serverChatWebSocket = new ServerChatWebSocket(builder, server, URI.create(WS_SERVER_URL + CHAT_WEBSOCKET_PATH + builder.getPersonalUser().getName() + SERVER_WEBSOCKET_PATH + server.getId()));
            builder.addServerChatWebSocket(server.getId(), serverChatWebSocket);
        }
    }

    /**
     * when click on home button the home or privateChat view fragment will be shown
     */
    private void onHomeButtonClick(View view) {
        if (builder.getState() == State.ServerView) {
            builder.setCurrentServer(null);
            updateServerRV();

            // show online user fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_user,
                    onlineUserController).commit();

            if (builder.getSelectedPrivateChat() == null) {
                // show home
                Toast.makeText(this, "to Home", Toast.LENGTH_SHORT).show();
                builder.setState(State.HomeView);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                        homeController).commit();

                // change to private chats fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_items,
                        privateChatsController).commit();
            } else {
                // show private chat messages
                Toast.makeText(this, "to Chats", Toast.LENGTH_SHORT).show();
                builder.setState(State.PrivateChatView);

                // change to private chats fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_items,
                        privateChatsController).commit();

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                        privateMessageController).commit();
            }

            updateHomeButtonColor();
        }
    }

    private void updateHomeButtonColor() {
        if (builder.getState() != State.ServerView) {
            // home clicked color
            button_Home.setCardBackgroundColor(ContextCompat.getColor(this, R.color.homeButtonClicked));
        } else {
            // home normal color
            button_Home.setCardBackgroundColor(ContextCompat.getColor(this, R.color.homeButtonNormal));
        }
    }

    /**
     * load and shows all online users
     * setup the system webSocket
     */
    public void loadOnlineUsers() {
        // Get Online User
        restClient.doGetOnlineUser(builder.getPersonalUser().getUserKey(), new RestClient.ResponseCallbackWithList() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(String status, ArrayList<ResponseWithJsonList.Data> dataArrayList) {
                for (ResponseWithJsonList.Data data : dataArrayList) {
                    String userName = data.getName();
                    String userId = data.getId();
                    String userDescription = data.getDescription();

                    if (!userName.equals(builder.getPersonalUser().getName())) {
                        builder.buildUser(userName, userId, userDescription);
                    } else {
                        builder.getPersonalUser().setId(userId);
                    }
                }
                builder.getOnlineUserController().updateOnlineUsersRV();
            }

            @Override
            public void onFailed(Throwable error) {
                System.out.print("Error: " + error.getMessage());
            }
        });
    }

    /**
     * shows all servers and setup handler
     */
    private void setupServersRecyclerView() {
        rv_server.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        serverRecyclerViewAdapter = new ServerRecyclerViewAdapter(this, builder);

        rv_server.setLayoutManager(layoutManager);
        rv_server.setAdapter(serverRecyclerViewAdapter);

        serverRecyclerViewAdapter.setOnItemClickListener(new ServerRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Server server) {
                onServerClicked(server);
            }

            @Override
            public void onItemLongClick(View view, Server server) {
                onServerLongClicked(server);
            }
        });
    }

    /**
     * short click on server
     */
    public void onServerClicked(Server server) {
        if (builder.getCurrentServer() == null || builder.getCurrentServer() != server) {

            String serverName = server.getName();
            Toast.makeText(MainActivity.this, serverName, Toast.LENGTH_LONG).show();

            Server oldServer = builder.getCurrentServer();
            builder.setCurrentServer(server);
            updateServerRV();

            if (builder.getState() == State.ServerView) {
                // if in server view
                serverItemsController.updateServerItemsFragment();

                // show members when serverMemberFragment is loaded yet
                serverMembersController.updateOnlineMembersRV();
                serverMembersController.updateOfflineMembersRV();
                serverMembersController.updateOnlineOfflineMemberCount();

                // state fragment container handle
                if (oldServer.getCurrentServerChannel() == null && server.getCurrentServerChannel() != null) {
                    // if serverView was loaded and server chatView has to be loaded
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                            serverMessageController).commit();
                } else if (oldServer.getCurrentServerChannel() != null && server.getCurrentServerChannel() != null) {
                    // if server chatView was loaded and server chatView has to be loaded
                    serverMessageController.notifyOnChannelChanged();
                } else if (oldServer.getCurrentServerChannel() != null && server.getCurrentServerChannel() == null) {
                    // if server chatView was loaded and server view has to be loaded
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                            serverController).commit();
                } else if (oldServer.getCurrentServerChannel() == null && server.getCurrentServerChannel() == null) {
                    // if server chatView was loaded and server view has to be loaded
                    serverController.updateServerFragment();
                }
            } else if (builder.getState() != State.ServerView) {
                // if coming from home or private view
                builder.setState(State.ServerView);
                updateHomeButtonColor();

                // change to server items fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_items,
                        serverItemsController).commit();

                if (server.getCurrentServerChannel() != null) {
                    // change to chat if existing
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                            serverMessageController).commit();
                } else {
                    // show empty server page if no chat was opened
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                            serverController).commit();
                }

                // show server members
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_user,
                        serverMembersController).commit();
            }
        }
    }

    /**
     * long click on server
     */
    private void onServerLongClicked(Server server) {
        String serverId = server.getId();
        Toast.makeText(MainActivity.this, serverId, Toast.LENGTH_LONG).show();
    }

    /**
     * button handler to create a server
     */
    private void onAddServerButtonClick(View view) {
        Toast.makeText(this, "add a new server", Toast.LENGTH_SHORT).show();

        // create bottomSheet for create server with all actions
        BottomSheetCreateServer bottomSheetCreateServer = new BottomSheetCreateServer(this, R.style.BottomSheetDialogTheme, builder, builder.getCurrentServer());
        bottomSheetCreateServer.show();
    }

    /**
     * short click on private chat
     * NOT LONGER NEEDED???
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_Home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                        homeController).commit();
                Toast.makeText(this, builder.getPersonalUser().getName(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_PrivateChat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                        privateMessageController).commit();
                Toast.makeText(this, builder.getPersonalUser().getUserKey(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_Server:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                        serverController).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * handle the android back pressed button
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(findViewById(R.id.nav_view_left))) {
            // if left drawer is open
            button_logout.callOnClick();
        } else if (drawer.isDrawerOpen(findViewById(R.id.nav_view_right))) {
            // if right drawer is open
            drawer.closeDrawer(findViewById(R.id.nav_view_right));
        } else if (!(drawer.isDrawerOpen(findViewById(R.id.nav_view_right)) && !(drawer.isDrawerOpen(findViewById(R.id.nav_view_right))))) {
            // if no drawer is open
            drawer.openDrawer(findViewById(R.id.nav_view_left));
        } else {
            super.onBackPressed();
        }
    }

    /**
     * handle the logout button action -> logout the user
     */
    private void onLogoutButtonClick(View view) {
        saveData();

        restClient.doLogout(builder.getPersonalUser().getUserKey(), new RestClient.ResponseCallbackWithObject() {
            @Override
            public void onSuccess(String status, ResponseWithJsonObject.Data data) {
                System.out.print(status);

                showLoginActivity();
            }

            @Override
            public void onFailed(Throwable error) {
                System.out.print("Error: " + error.getMessage());
            }
        });
    }

    /**
     * update the server recyclerView
     */
    private void updateServerRV() {
        runOnUiThread(() -> serverRecyclerViewAdapter.notifyDataSetChanged());
    }

    /**
     * saves the data to sharedPreferences
     */
    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        ArrayList<Channel> channelArrayList = new ArrayList<>();
        for (Channel channel : builder.getPersonalUser().getPrivateChat()) {
            Channel newChannel = new Channel().setName(channel.getName()).setId(channel.getId()).setMessages(channel.getMessages()).setUnreadMessagesCounter(channel.getUnreadMessagesCounter());
            channelArrayList.add(newChannel);
        }
        String json = gson.toJson(channelArrayList);
        editor.putString(builder.getPersonalUser().getName(), json);
        editor.apply();
    }

    /**
     * loads the data from sharedPreferences
     */
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(builder.getPersonalUser().getName(), null);
        Type type = new TypeToken<ArrayList<Channel>>() {
        }.getType();
        ArrayList<Channel> mExampleList = gson.fromJson(json, type);
        if (mExampleList != null) {
            builder.getPersonalUser().withPrivateChat(mExampleList);
        }
    }

    public void showLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_exit_backwards, R.anim.activity_enter_backwards);
    }

    public void updateOnlineUserRV() {
    }

    public void showPrivateMessages() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                builder.getPrivateMessageController()).commit();
    }

    public void showServerMessages() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                builder.getServerMessageController()).commit();
    }

    public void showEmptyServerFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                serverController).commit();
    }

    private void showOnlineUsers() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_user,
                onlineUserController).commit();
    }

    public void closeLeftDrawer() {
        drawer.closeDrawer(findViewById(R.id.nav_view_left));
    }

    public void closeRightDrawer() {
        drawer.closeDrawer(findViewById(R.id.nav_view_right));
    }
}
package com.accord;

import static com.accord.util.Constants.CHAT_WEBSOCKET_PATH;
import static com.accord.util.Constants.SERVER_SYSTEM_WEBSOCKET_PATH;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accord.adapter.OnlineUserRecyclerViewAdapter;
import com.accord.adapter.ServerRecyclerViewAdapter;
import com.accord.model.Categories;
import com.accord.model.Channel;
import com.accord.model.Message;
import com.accord.model.Server;
import com.accord.model.ServerChannel;
import com.accord.model.User;
import com.accord.net.rest.RestClient;
import com.accord.net.webSocket.chatSockets.PrivateChatWebSocket;
import com.accord.net.webSocket.systemSockets.ServerSystemWebSocket;
import com.accord.net.webSocket.systemSockets.SystemWebSocket;
import com.accord.ui.home.HomeFragment;
import com.accord.ui.chatMessages.PrivateMessageFragment;
import com.accord.ui.home.PrivateChatsFragment;
import com.accord.ui.server.ServerFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

// upgraded gradle from 4.2.2 to 7.0.2
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static ModelBuilder builder;
    private RestClient restClient;
    private DrawerLayout drawer;
    private NavigationView navigationViewLeft;
    private HomeFragment homeController;
    private PrivateChatsFragment privateChatsController;
    private PrivateMessageFragment privateMessageController;
    private ServerFragment serverController;

    public void showMessages() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                builder.getPrivateMessageController()).commit();
    }

    public void closeLeftDrawer() {
        drawer.closeDrawer(findViewById(R.id.nav_view_left));
    }

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
    private RecyclerView rv_onlineUser;
    private OnlineUserRecyclerViewAdapter onlineUserRecyclerViewAdapter;
    private ServerRecyclerViewAdapter serverRecyclerViewAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String KEY_PREFS = "privateChats";

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
        builder.buildPersonalUser(username, userKey);
        builder.setState(State.HomeView);
        builder.setMainActivity(this);

        restClient = new RestClient();
        restClient.setup();

        drawer = findViewById(R.id.drawer_layout);
        navigationViewLeft = findViewById(R.id.nav_view_left);
        navigationViewRight = findViewById(R.id.nav_view_right);
        navigationViewLeft.setNavigationItemSelectedListener(this);

        // Setup navigation and screen when start
        homeController = new HomeFragment(builder);
        privateChatsController = new PrivateChatsFragment(builder);
        privateMessageController = new PrivateMessageFragment(builder);
        serverController = new ServerFragment(builder);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    homeController).commit();
            navigationViewLeft.setCheckedItem(R.id.nav_Home);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_items,
                    privateChatsController).commit();
        }

        ////////////////////////////////////////////////////

        rv_server = navigationViewLeft.findViewById(R.id.rv_server);
        rv_onlineUser = navigationViewRight.findViewById(R.id.rv_onlineUser);
        button_logout = navigationViewLeft.findViewById(R.id.button_logout);
        button_Home = navigationViewLeft.findViewById(R.id.button_Home);
        button_addServer = navigationViewLeft.findViewById(R.id.button_add);
        text_username = navigationViewLeft.findViewById(R.id.text_username);
        text_userKey = navigationViewLeft.findViewById(R.id.text_userKey);

        text_username.setText(builder.getPersonalUser().getName());
        text_userKey.setText(builder.getPersonalUser().getUserKey());

        builder.setHomeController(homeController);
        builder.setPrivateMessageController(privateMessageController);
        builder.setServerController(serverController);

        button_logout.setOnClickListener(this::onLogoutButtonClick);
        button_Home.setOnClickListener(this::onHomeButtonClick);
        button_addServer.setOnClickListener(this::onAddServerButtonClick);

        sharedPreferences = this.getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // load all sharedPreferences
        loadData();

        // setup RecyclerViews with listener
        setupOnlineUserRecyclerView();
        setupServersRecyclerView();

        // setup webSockets for system messages and private chats
        setupWebSockets();

        // Get online users & servers and view them
        loadOnlineUsers();
        loadServer(() -> {
            System.out.println("All server loaded!");
            // after loading all servers create the webSockets
            setupServerSystemWebSocket();
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
        restClient.doGetServer(builder.getPersonalUser().getUserKey(), new RestClient.GetCallbackWithList() {
            @Override
            public void onSuccess(String status, List data) {
                System.out.print(status);
                System.out.print(data);
                for (int i = 0; i < data.size(); i++) {
                    Map<String, String> serverMap = (Map<String, String>) data.get(i);
                    String serverName = serverMap.get("name");
                    String serverId = serverMap.get("id");

                    Server server = builder.buildServer(serverName, serverId);
                    loadServerUsers(server, new ServerUserCallback() {
                        @Override
                        public void onSuccess(String status) {
                            loadCategories(server, new CategoriesLoadedCallback() {
                                @Override
                                public void onSuccess(String status) {
                                    fullyLoadedCallback.onSuccess();
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

    private void loadServerUsers(Server server, ServerUserCallback serverUserCallback) {
        restClient.doGetServerUsers(server.getId(), builder.getPersonalUser().getUserKey(), new RestClient.GetCallbackWithObject() {

            @Override
            public void onSuccess(String status, Object data) {
                System.out.print(status);
                System.out.print(data);

                try {
                    Gson gson = new Gson();
                    JSONObject dataJSON = new JSONObject(gson.toJsonTree(data).getAsJsonObject().toString());

                    server.setOwner(dataJSON.getString("owner"));

                    JSONArray members = dataJSON.getJSONArray("members");

                    for (int i = 0; i < members.length(); i++) {
                        String id = members.getJSONObject(i).getString("id");
                        String description = members.getJSONObject(i).getString("description");
                        String name = members.getJSONObject(i).getString("name");
                        boolean online = Boolean.getBoolean(members.getJSONObject(i).getString("online"));
                        builder.buildServerUser(server, name, id, online, description);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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

    private void loadCategories(Server server, CategoriesLoadedCallback categoriesLoadedCallback) {
        restClient.doGetCategories(server.getId(), builder.getPersonalUser().getUserKey(), new RestClient.GetCallbackWithList() {

            private int loadedCategories = 0;

            @Override
            public void onSuccess(String status, List data) {
                System.out.print(status);
                System.out.print(data);

                for (int i = 0; i < data.size(); i++) {
                    Map<String, String> categoryMap = (Map<String, String>) data.get(i);
                    String categoryName = categoryMap.get("name");
                    String categoryId = categoryMap.get("id");

                    Categories category = new Categories().setId(categoryId).setName(categoryName);
                    server.withCategories(category);

                    loadChannel(server, category, status1 -> {
                        loadedCategories++;
                        if (loadedCategories == data.size()) {
                            loadedCategories = 0;
                            categoriesLoadedCallback.onSuccess(status1);
                        }
                    });
                }
                updateServerRV();
//                fullyLoadedCallback.onSuccess();
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
        restClient.doGetChannels(server.getId(), category.getId(), builder.getPersonalUser().getUserKey(), new RestClient.GetCallbackWithList() {

            private int loadedChannel = 0;

            @Override
            public void onSuccess(String status, List data) {
                System.out.print(status);
                System.out.print(data);
                for (int i = 0; i < data.size(); i++) {
                    Map<String, Object> channelsMap = (Map<String, Object>) data.get(i);
                    String channelName = (String) channelsMap.get("name");
                    String channelId = (String) channelsMap.get("id");
                    String channelType = (String) channelsMap.get("type");
                    boolean channelIsPrivileged = (boolean) channelsMap.get("privileged");
                    ServerChannel serverChannel = new ServerChannel();
                    serverChannel.setId(channelId);
                    serverChannel.setName(channelName);
                    serverChannel.setType(channelType);
                    serverChannel.setPrivilege(channelIsPrivileged);
                    category.withChannel(serverChannel);

                    ArrayList<String> audioMemberIds = (ArrayList<String>) channelsMap.get("audioMembers");
                    for (String id : audioMemberIds) {
                        for (User user : server.getUser()) {
                            if (user.getId().equals(id)) {
                                serverChannel.withAudioMember(user);
                            }
                        }
                    }

                    ArrayList<String> membersIds = (ArrayList<String>) channelsMap.get("members");
                    for (String id : membersIds) {
                        for (User user : server.getUser()) {
                            if (user.getId().equals(id)) {
                                serverChannel.withPrivilegedUsers(user);
                            }
                        }
                    }
                    if (serverChannel.getType().equals("text")) {
                        loadMessages(server, category, serverChannel, status1 -> {
                            loadedChannel++;
                            if (loadedChannel == data.size()) {
                                loadedChannel = 0;
                                channelLoadedCallback.onSuccess(status1);
                            }
                        });
                    } else {
                        loadedChannel++;
                        if (loadedChannel == data.size()) {
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
        restClient.doGetMessages(new Date().getTime(), server.getId(), category.getId(), serverChannel.getId(), builder.getPersonalUser().getUserKey(), new RestClient.GetCallbackWithList() {

            @Override
            public void onSuccess(String status, List data) {
                System.out.print(status);
                System.out.print(data);
                for (int i = 0; i < data.size(); i++) {
                    Map<String, Object> messageMap = (Map<String, Object>) data.get(i);
                    String from = (String) messageMap.get("from");
                    double timestampDouble = (double) messageMap.get("timestamp");
                    long timestamp = (long) timestampDouble;
                    String text = (String) messageMap.get("text");
                    String id = (String) messageMap.get("id");

                    Message message = new Message().setMessage(text).setFrom(from).setTimestamp(timestamp).setId(id);
                    serverChannel.withMessage(message);
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

    private void setupServerSystemWebSocket() {
        for (Server server : builder.getPersonalUser().getServer()) {
            ServerSystemWebSocket serverSystemWebSocket = new ServerSystemWebSocket(builder, URI.create(WS_SERVER_URL + SERVER_SYSTEM_WEBSOCKET_PATH + server.getId()));
            builder.addServerSystemWebSocket(server.getId(), serverSystemWebSocket);
        }
    }

    /**
     * when click on home button the home or privateChat view fragment will be shown
     */
    private void onHomeButtonClick(View view) {
        if (builder.getState() == State.ServerView) {
            builder.setCurrentServer(null);
            updateServerRV();

            if (builder.getSelectedPrivateChat() == null) {
                // show home
                Toast.makeText(this, "to Home", Toast.LENGTH_SHORT).show();
                builder.setState(State.HomeView);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        homeController).commit();
            } else {
                // show private chat messages
                Toast.makeText(this, "to Chats", Toast.LENGTH_SHORT).show();
                builder.setState(State.PrivateChatView);
                privateChatsController.updatePrivateChatsRV();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        privateMessageController).commit();
            }

            // change to private chats fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_items,
                    privateChatsController).commit();
            privateChatsController.updatePrivateChatsRV();
        }
    }

    /**
     * load and shows all online users
     * setup the system webSocket
     */
    public void loadOnlineUsers() {
        // Get Online User
        restClient.doGetOnlineUser(builder.getPersonalUser().getUserKey(), new RestClient.GetCallbackWithList() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(String status, List data) {
                for (int i = 0; i < data.size(); i++) {
                    Map<String, String> userMap = (Map<String, String>) data.get(i);
                    String userName = userMap.get("name");
                    String userId = userMap.get("id");
                    String userDescription = userMap.get("description");

                    //if (!userName.equals(modelBuilder.getPersonalUser().getName())) {
                    builder.buildUser(userName, userId);
                    //}
                }
                updateOnlineUserRV();
            }

            @Override
            public void onFailed(Throwable error) {
                System.out.print("Error: " + error.getMessage());
            }
        });
    }

    /**
     * shows all online users and setup handler
     */
    private void setupOnlineUserRecyclerView() {
        rv_onlineUser.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        onlineUserRecyclerViewAdapter = new OnlineUserRecyclerViewAdapter(this, builder);

        rv_onlineUser.setLayoutManager(layoutManager);
        rv_onlineUser.setAdapter(onlineUserRecyclerViewAdapter);

        onlineUserRecyclerViewAdapter.setOnItemClickListener(new OnlineUserRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, User user) {
                if (user != null) {
                    onOnlineUserClicked(user);
                }
            }

            @Override
            public void onItemLongClick(View view, User user) {
                if (user != null) {
                    onOnlineUserLongClicked(user);
                }
            }
        });
    }

    /**
     * short click on online user
     */
    private void onOnlineUserClicked(User user) {
        String userName = user.getName();
        //Toast.makeText(MainActivity.this, userName, Toast.LENGTH_LONG).show();

        Channel currentChannel = builder.getSelectedPrivateChat();
        boolean chatExisting = false;
        String selectedUserName = user.getName();
        String selectUserId = user.getId();

        for (Channel channel : builder.getPersonalUser().getPrivateChat()) {
            if (channel.getName().equals(selectedUserName)) {
                builder.setSelectedPrivateChat(channel);
                privateChatsController.updatePrivateChatsRV();
                privateMessageController.notifyOnChatChanged();
                chatExisting = true;
                break;
            }
        }

        if ((builder.getState() == State.HomeView && !chatExisting)) {
            builder.setState(State.PrivateChatView);
            builder.setSelectedPrivateChat(new Channel().setName(selectedUserName).setId(selectUserId));
            builder.getPersonalUser().withPrivateChat(builder.getSelectedPrivateChat());
            chatExisting = true;
            privateChatsController.updatePrivateChatsRV();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    privateMessageController).commit();
        }

        if (builder.getState() == State.PrivateChatView && !chatExisting) {
            builder.setSelectedPrivateChat(new Channel().setName(selectedUserName).setId(selectUserId));
            builder.getPersonalUser().withPrivateChat(builder.getSelectedPrivateChat());
            chatExisting = true;
            privateChatsController.updatePrivateChatsRV();
            privateMessageController.notifyOnChatChanged();
        }
        drawer.closeDrawer(findViewById(R.id.nav_view_right));
    }

    /**
     * long click on online user
     */
    private void onOnlineUserLongClicked(User user) {
        String userId = user.getId();
        Toast.makeText(MainActivity.this, userId, Toast.LENGTH_LONG).show();
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
    private void onServerClicked(Server server) {
        String serverName = server.getName();
        Toast.makeText(MainActivity.this, serverName, Toast.LENGTH_LONG).show();

        builder.setCurrentServer(server);
        updateServerRV();

        if (builder.getState() != State.ServerView) {
            builder.setState(State.ServerView);
            privateChatsController.updatePrivateChatsRV();
            //rv_serverChannel.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    serverController).commit();
        } else {
            serverController.updateServerFragment();
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
        // TODO Server erstellen
        Toast.makeText(this, "add Server", Toast.LENGTH_SHORT).show();
    }

    /**
     * short click on private chat
     * NOT LONGER NEEDED???
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_Home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        homeController).commit();
                Toast.makeText(this, builder.getPersonalUser().getName(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_PrivateChat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        privateMessageController).commit();
                Toast.makeText(this, builder.getPersonalUser().getUserKey(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_Server:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
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

        restClient.doLogout(builder.getPersonalUser().getUserKey(), new RestClient.PostCallback() {
            @Override
            public void onSuccess(String status, Map<String, String> data) {
                System.out.print(status);
                System.out.print(data);

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
        runOnUiThread(() -> onlineUserRecyclerViewAdapter.notifyDataSetChanged());
    }
}
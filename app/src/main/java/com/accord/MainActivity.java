package com.accord;

import static com.accord.util.Constants.CHAT_WEBSOCKET_PATH;
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
import com.accord.adapter.PrivateChatRecyclerViewAdapter;
import com.accord.adapter.ServerRecyclerViewAdapter;
import com.accord.model.Channel;
import com.accord.model.Message;
import com.accord.model.Server;
import com.accord.model.User;
import com.accord.net.RestClient;
import com.accord.net.WSCallback;
import com.accord.net.WebSocketClient;
import com.accord.ui.home.HomeFragment;
import com.accord.ui.privateChat.PrivateMessageFragment;
import com.accord.ui.server.ServerFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.Session;

// upgraded gradle from 4.2.2 to 7.0.2
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static ModelBuilder modelBuilder;
    private RestClient restClient;
    private WebSocketClient USER_CLIENT;
    private WebSocketClient privateChatWebSocketClient;
    private DrawerLayout drawer;
    private NavigationView navigationViewLeft;
    private HomeFragment homeController;
    private PrivateMessageFragment privateMessageController;
    private ServerFragment serverController;

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
    private RecyclerView rv_privateChats;
    private OnlineUserRecyclerViewAdapter onlineUserRecyclerViewAdapter;
    private PrivateChatRecyclerViewAdapter privateChatsRecyclerViewAdapter;
    private ServerRecyclerViewAdapter serverRecyclerViewAdapter;
    private SimpleDateFormat timeFormatter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String KEY_PREFS = "privateChats";

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get ModelBuilder
        Gson gson = new Gson();
        String modelBuilderAsAString = getIntent().getStringExtra("ModelBuilder");
        modelBuilder = gson.fromJson(modelBuilderAsAString, ModelBuilder.class);
        modelBuilder.setState(State.HomeView);

        restClient = new RestClient();
        restClient.setup();

        drawer = findViewById(R.id.drawer_layout);
        navigationViewLeft = findViewById(R.id.nav_view_left);
        navigationViewRight = findViewById(R.id.nav_view_right);
        navigationViewLeft.setNavigationItemSelectedListener(this);

        // Setup navigation and screen when start
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment(modelBuilder)).commit();
            navigationViewLeft.setCheckedItem(R.id.nav_Home);
        }

        ////////////////////////////////////////////////////

        rv_server = navigationViewLeft.findViewById(R.id.rv_server);
        rv_onlineUser = navigationViewRight.findViewById(R.id.rv_onlineUser);
        rv_privateChats = navigationViewLeft.findViewById(R.id.rv_privateChats);
        button_logout = navigationViewLeft.findViewById(R.id.button_logout);
        button_Home = navigationViewLeft.findViewById(R.id.button_Home);
        button_addServer = navigationViewLeft.findViewById(R.id.button_add);
        text_username = navigationViewLeft.findViewById(R.id.text_username);
        text_userKey = navigationViewLeft.findViewById(R.id.text_userKey);

        text_username.setText(modelBuilder.getPersonalUser().getName());
        text_userKey.setText(modelBuilder.getPersonalUser().getUserKey());

        homeController = new HomeFragment(modelBuilder);
        privateMessageController = new PrivateMessageFragment(modelBuilder);
        serverController = new ServerFragment(modelBuilder);

        button_logout.setOnClickListener(this::onLogoutButtonClick);
        button_Home.setOnClickListener(this::onHomeButtonClick);
        button_addServer.setOnClickListener(this::onAddServerButtonClick);

        timeFormatter = new SimpleDateFormat("HH:mm");

        sharedPreferences = this.getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // load all sharedPreferences
        loadData();

        // Get online users and view them and start systemWebSocket
        showUsers();
        // setup privateChats to view with listener
        setupPrivateChatRecyclerView();
        // setup privateChatWebSocket with handlers
        setupPrivateChatWebSocket();

        restClient.doGetServer(modelBuilder.getPersonalUser().getUserKey(), new RestClient.GetCallback() {
            @Override
            public void onSuccess(String status, List data) {
                System.out.print(status);
                System.out.print(data);
                ArrayList<Server> onlineServers = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    Map<String, String> serverMap = (Map<String, String>) data.get(i);
                    String serverName = serverMap.get("name");
                    String serverId = serverMap.get("id");
                    System.out.print("XXX");

                    Server server = modelBuilder.buildServer(serverName, serverId);
                    onlineServers.add(server);
                }
                for (Server server : modelBuilder.getPersonalUser().getServer()) {
                    if (!onlineServers.contains(server)) {
                        modelBuilder.getPersonalUser().withoutServer(server);
                    }
                }
                setupServersRecyclerView();
            }

            @Override
            public void onFailed(Throwable error) {
                System.out.print("Error: " + error.getMessage());
            }
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

    /**
     * when click on home button the home or privateChat view fragment will be shown
     */
    private void onHomeButtonClick(View view) {
        if (modelBuilder.getState() != State.HomeView && modelBuilder.getPersonalUser().getPrivateChat().size() == 0) {
            Toast.makeText(this, "to Home", Toast.LENGTH_SHORT).show();
            modelBuilder.setState(State.HomeView);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    homeController).commit();
        } else if (modelBuilder.getState() != State.HomeView && modelBuilder.getPersonalUser().getPrivateChat().size() > 0) {
            Toast.makeText(this, "to Chats", Toast.LENGTH_SHORT).show();
            modelBuilder.setState(State.PrivateChatView);
            updatePrivateChatRecyclerView();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    privateMessageController).commit();
        }
    }

    /**
     * setup the private chat webSocket with handler
     */
    private void setupPrivateChatWebSocket() {
        if (modelBuilder.getPrivateChatWebSocketClient() == null) {
            privateChatWebSocketClient = new WebSocketClient(modelBuilder, URI.
                    create(WS_SERVER_URL + CHAT_WEBSOCKET_PATH + modelBuilder.
                            getPersonalUser().getName().replace(" ", "+")), new WSCallback() {
                /**
                 * handles server response
                 *
                 * @param msg is the response from the server as a JsonStructure
                 */
                @Override
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
                            if (msg.getString("from").equals(modelBuilder.getPersonalUser().getName())) {
                                channelName = msg.getString("to");
                                message = new Message().setMessage(msg.getString("message")).
                                        setFrom(msg.getString("from")).
                                        setTimestamp(msg.getInt("timestamp")).setCurrentTime(currentTime);
                                privateMessageController.clearMessageField();
                            } else { // currentUser received
                                channelName = msg.getString("from");
                                message = new Message().setMessage(msg.getString("message")).
                                        setFrom(msg.getString("from")).
                                        setTimestamp(msg.getInt("timestamp")).setCurrentTime(currentTime);
                            }
                            for (Channel channel : modelBuilder.getPersonalUser().getPrivateChat()) {
                                if (channel.getName().equals(channelName)) {
                                    channel.withMessage(message);
                                    if (modelBuilder.getSelectedPrivateChat() == null || channel != modelBuilder.getSelectedPrivateChat()) {
                                        channel.setUnreadMessagesCounter(channel.getUnreadMessagesCounter() + 1);
                                    }
                                    updatePrivateChatRecyclerView();
                                    privateMessageController.updatePrivateMessagesFragment();
                                    newChat = false;
                                    break;
                                }
                            }
                            if (newChat) {
                                String userId = "";
                                for (User user : modelBuilder.getPersonalUser().getUser()) {
                                    if (user.getName().equals(channelName)) {
                                        userId = user.getId();
                                    }
                                }
                                Channel channel = new Channel().setId(userId).setName(channelName).withMessage(message).setUnreadMessagesCounter(1);
                                modelBuilder.getPersonalUser().withPrivateChat(channel);
                                updatePrivateChatRecyclerView();
                                privateMessageController.updatePrivateMessagesFragment();
                            }
                            if (privateMessageController != null) {
                                //privateMessageController.printMessage(message); //PRINT MESSAGE
                            }
                        }
                        if (msg.has("action") && msg.getString("action").equals("info")) {
                            String errorTitle;
                            String serverMessage = msg.getJSONObject("data").getString("message");
                            if (serverMessage.equals("This is not your username.")) {
                                System.out.print("This is not your username.");
                                Toast.makeText(MainActivity.this, "This is not your username.", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onClose(Session session, CloseReason closeReason) {
                    System.out.println(closeReason.getCloseCode().toString());
                    if (!closeReason.getCloseCode().toString().equals("NORMAL_CLOSURE")) {
                        System.out.print(closeReason);
                        Toast.makeText(MainActivity.this, "NORMAL_CLOSURE", Toast.LENGTH_LONG).show();
                    }
                }
            });
            modelBuilder.setPrivateChatWebSocketClient(privateChatWebSocketClient);
        } else {
            privateChatWebSocketClient = modelBuilder.getPrivateChatWebSocketClient();
        }
    }

    /**
     * load and shows all online users
     * setup the system webSocket
     */
    public void showUsers() {
        // Get Online User
        restClient.doGetOnlineUser(modelBuilder.getPersonalUser().getUserKey(), new RestClient.GetCallback() {
            @Override
            public void onSuccess(String status, List data) {
                for (int i = 0; i < data.size(); i++) {
                    Map<String, String> userMap = (Map<String, String>) data.get(i);
                    String userName = userMap.get("name");
                    String userId = userMap.get("id");

                    //if (!userName.equals(modelBuilder.getPersonalUser().getName())) {
                    modelBuilder.buildUser(userName, userId);
                    //}
                }
                setupOnlineUserRecyclerView();
                startWebSocketConnection();
            }

            @Override
            public void onFailed(Throwable error) {
                System.out.print("Error: " + error.getMessage());
            }
        });
    }

    /**
     * setup the system webSocket
     */
    private void startWebSocketConnection() {
        try {
            USER_CLIENT = new WebSocketClient(modelBuilder, new URI(WS_SERVER_URL + SYSTEM_WEBSOCKET_PATH), new WSCallback() {
                @Override
                public void handleMessage(JSONObject msg) {
                    try {
                        System.out.println("msg: " + msg);
                        String userAction = msg.getString("action");
                        JSONObject jsonData = msg.getJSONObject("data");
                        String userName = jsonData.getString("name");
                        String userId = jsonData.getString("id");

                        if (userAction.equals("userJoined")) {
                            modelBuilder.buildUser(userName, userId);
                        }
                        if (userAction.equals("userLeft")) {
                            if (userName.equals(modelBuilder.getPersonalUser().getName())) {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.activity_exit_backwards, R.anim.activity_enter_backwards);
                            }

                            List<User> userList = modelBuilder.getPersonalUser().getUser();
                            User removeUser = modelBuilder.buildUser(userName, userId);
                            if (userList.contains(removeUser)) {
                                modelBuilder.getPersonalUser().withoutUser(removeUser);
                            }
                        }
                        //modelBuilder.getPersonalUser().getUser().sort(new SortUser());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onlineUserRecyclerViewAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(Session session, CloseReason closeReason) {
                    System.out.print(closeReason);
                    Toast.makeText(MainActivity.this, "NORMAL_CLOSURE", Toast.LENGTH_LONG).show();
                }
            });
            modelBuilder.setUSER_CLIENT(USER_CLIENT);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * shows all online users and setup handler
     */
    private void setupOnlineUserRecyclerView() {
        rv_onlineUser.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        onlineUserRecyclerViewAdapter = new OnlineUserRecyclerViewAdapter(this, modelBuilder);

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

        Channel currentChannel = modelBuilder.getSelectedPrivateChat();
        boolean chatExisting = false;
        String selectedUserName = user.getName();
        String selectUserId = user.getId();

        for (Channel channel : modelBuilder.getPersonalUser().getPrivateChat()) {
            if (channel.getName().equals(selectedUserName)) {
                modelBuilder.setSelectedPrivateChat(channel);
                updatePrivateChatRecyclerView();
                privateMessageController.changePrivateChatFragment();
                chatExisting = true;
                break;
            }
        }

        if ((modelBuilder.getState() == State.HomeView && !chatExisting)) {
            modelBuilder.setState(State.PrivateChatView);
            modelBuilder.setSelectedPrivateChat(new Channel().setName(selectedUserName).setId(selectUserId));
            modelBuilder.getPersonalUser().withPrivateChat(modelBuilder.getSelectedPrivateChat());
            chatExisting = true;
            updatePrivateChatRecyclerView();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    privateMessageController).commit();
        }

        if (modelBuilder.getState() == State.PrivateChatView && !chatExisting) {
            modelBuilder.setSelectedPrivateChat(new Channel().setName(selectedUserName).setId(selectUserId));
            modelBuilder.getPersonalUser().withPrivateChat(modelBuilder.getSelectedPrivateChat());
            chatExisting = true;
            updatePrivateChatRecyclerView();
            privateMessageController.changePrivateChatFragment();
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
     * shows all private chats and setup handler
     */
    private void setupPrivateChatRecyclerView() {
        rv_privateChats.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        privateChatsRecyclerViewAdapter = new PrivateChatRecyclerViewAdapter(this, modelBuilder);

        rv_privateChats.setLayoutManager(layoutManager);
        rv_privateChats.setAdapter(privateChatsRecyclerViewAdapter);

        privateChatsRecyclerViewAdapter.setOnItemClickListener(new PrivateChatRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Channel channel) {
                onPrivateChatClicked(channel);
            }

            @Override
            public void onItemLongClick(View view, Channel channel) {
                onPrivateChatLongClicked(channel);
            }
        });
    }

    /**
     * short click on private chat
     */
    private void onPrivateChatClicked(Channel selectedChannel) {
        String userName = selectedChannel.getName();
        Toast.makeText(MainActivity.this, userName, Toast.LENGTH_LONG).show();

        modelBuilder.setSelectedPrivateChat(selectedChannel);
        if (modelBuilder.getState() == State.HomeView) {
            modelBuilder.setState(State.PrivateChatView);
            updatePrivateChatRecyclerView();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    privateMessageController).commit();
        } else {
            updatePrivateChatRecyclerView();
            privateMessageController.changePrivateChatFragment();
        }
        drawer.closeDrawer(findViewById(R.id.nav_view_left));
    }

    /**
     * long click on private chat
     */
    private void onPrivateChatLongClicked(Channel chat) {
        String chatId = chat.getId();
        Toast.makeText(MainActivity.this, chatId, Toast.LENGTH_LONG).show();
    }

    /**
     * shows all servers and setup handler
     */
    private void setupServersRecyclerView() {
        rv_server.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        serverRecyclerViewAdapter = new ServerRecyclerViewAdapter(this, modelBuilder);

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

        modelBuilder.setCurrentServer(server);
        updateServerRecyclerView();

        if (modelBuilder.getState() != State.ServerView) {
            modelBuilder.setState(State.ServerView);
            updatePrivateChatRecyclerView();
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
                Toast.makeText(this, modelBuilder.getPersonalUser().getName(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_PrivateChat:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        privateMessageController).commit();
                Toast.makeText(this, modelBuilder.getPersonalUser().getUserKey(), Toast.LENGTH_SHORT).show();
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

        restClient.doLogout(modelBuilder.getPersonalUser().getUserKey(), new RestClient.PostCallback() {
            @Override
            public void onSuccess(String status, Map<String, String> data) {
                System.out.print(status);
                System.out.print(data);

                ArrayList<Channel> list = new ArrayList<>(modelBuilder.getPersonalUser().getPrivateChat());
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_exit_backwards, R.anim.activity_enter_backwards);
            }

            @Override
            public void onFailed(Throwable error) {
                System.out.print("Error: " + error.getMessage());
            }
        });
    }

    /**
     * update the private chat recyclerView and set visible or not
     */
    private void updatePrivateChatRecyclerView() {
        if (modelBuilder.getState() != State.ServerView) {
            if (rv_privateChats.getVisibility() == View.INVISIBLE) {
                rv_privateChats.setVisibility(View.VISIBLE);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    privateChatsRecyclerViewAdapter.notifyDataSetChanged();
                }
            });
        } else {
            rv_privateChats.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * update the server recyclerView
     */
    private void updateServerRecyclerView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * saves the data to sharedPreferences
     */
    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        ArrayList<Channel> channelArrayList = new ArrayList<>(modelBuilder.getPersonalUser().getPrivateChat().size());
        for (Channel channel : modelBuilder.getPersonalUser().getPrivateChat()) {
            Channel newChannel = new Channel().setName(channel.getName()).setId(channel.getId()).setMessages(channel.getMessages()).setUnreadMessagesCounter(channel.getUnreadMessagesCounter());
            channelArrayList.add(newChannel);
        }
        String json = gson.toJson(channelArrayList);
        editor.putString(modelBuilder.getPersonalUser().getName(), json);
        editor.apply();
    }

    /**
     * loads the data from sharedPreferences
     */
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(modelBuilder.getPersonalUser().getName(), null);
        Type type = new TypeToken<ArrayList<Channel>>() {
        }.getType();
        ArrayList<Channel> mExampleList = gson.fromJson(json, type);
        if (mExampleList != null) {
            modelBuilder.getPersonalUser().withPrivateChat(mExampleList);
        }
    }
}
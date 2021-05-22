package com.accord;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accord.adapter.OnlineUserRecyclerViewAdapter;
import com.accord.model.Server;
import com.accord.net.RestClient;
import com.accord.ui.home.HomeFragment;
import com.accord.ui.privateChat.PrivateChatFragment;
import com.accord.ui.server.ServerFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static ModelBuilder modelBuilder;
    private RestClient restClient;
    private DrawerLayout drawer;
    private NavigationView navigationViewLeft;
    private HomeFragment homeController;
    private PrivateChatFragment privateChatController;
    private ServerFragment serverController;

    private TextView text_username;
    private TextView text_userKey;
    private Button button_logout;
    private NavigationView navigationViewRight;
    private RecyclerView rv_server;
    private RecyclerView rv_onlineUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get ModelBuilder
        Gson gson = new Gson();
        String modelBuilderAsAString = getIntent().getStringExtra("ModelBuilder");
        modelBuilder = gson.fromJson(modelBuilderAsAString, ModelBuilder.class);

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
        button_logout = navigationViewLeft.findViewById(R.id.button_logout);
        text_username = navigationViewLeft.findViewById(R.id.text_username);
        text_userKey = navigationViewLeft.findViewById(R.id.text_userKey);

        text_username.setText(modelBuilder.getPersonalUser().getName());
        text_userKey.setText(modelBuilder.getPersonalUser().getUserKey());

        homeController = new HomeFragment(modelBuilder);
        privateChatController = new PrivateChatFragment(modelBuilder);
        serverController = new ServerFragment(modelBuilder);

        button_logout.setOnClickListener(this::onLogoutButtonClick);

        // Get Online User
        restClient.doGetOnlineUser(modelBuilder.getPersonalUser().getUserKey(), new RestClient.GetCallback() {
            @Override
            public void onSuccess(String status, List data) {
                System.out.print(status);
                System.out.print(data);
                ArrayList<Server> onlineServers = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    Map<String, String> userMap = (Map<String, String>) data.get(i);
                    String userName = userMap.get("name");
                    String userId = userMap.get("id");

                    //if (!userName.equals(modelBuilder.getPersonalUser().getName())) {
                    modelBuilder.buildUser(userName, userId);
                    //}
                }
                updateOnlineUserListView();
            }

            @Override
            public void onFailed(Throwable error) {
                System.out.print("Error: " + error.getMessage());
            }
        });

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close) {
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

    private void updateOnlineUserListView() {
        rv_onlineUser.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        OnlineUserRecyclerViewAdapter onlineUserAdapter = new OnlineUserRecyclerViewAdapter(this, modelBuilder);

        rv_onlineUser.setLayoutManager(layoutManager);
        rv_onlineUser.setAdapter(onlineUserAdapter);

        onlineUserAdapter.setOnItemClickListener(new OnlineUserRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {

                String userName = modelBuilder.getPersonalUser().getUser().get(position).getName();
                String userId = modelBuilder.getPersonalUser().getUser().get(position).getId();
                Toast.makeText(MainActivity.this, modelBuilder.getPersonalUser().getUser().get(position).getName(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onItemLongClick(int position, View view) {
                Toast.makeText(MainActivity.this, modelBuilder.getPersonalUser().getUser().get(position).getId(), Toast.LENGTH_LONG).show();
            }
        });
    }

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
                        privateChatController).commit();
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void onLogoutButtonClick(View view) {
        restClient.doLogout(modelBuilder.getPersonalUser().getUserKey(), new RestClient.PostCallback() {
            @Override
            public void onSuccess(String status, Map<String, String> data) {
                System.out.print(status);
                System.out.print(data);

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


    public static ModelBuilder getModelBuilder() {
        return modelBuilder;
    }
}
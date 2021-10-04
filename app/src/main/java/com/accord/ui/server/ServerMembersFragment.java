package com.accord.ui.server;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.adapter.rightDrawer.ServerOfflineMemberRecyclerViewAdapter;
import com.accord.adapter.rightDrawer.ServerOnlineMemberRecyclerViewAdapter;
import com.accord.model.User;

public class ServerMembersFragment extends Fragment {
    private ModelBuilder builder;
    private Context context;

    private RecyclerView rv_serverMembersOnline;
    private TextView tv_onlineUserTitleCounter;
    private RecyclerView rv_serverMembersOffline;
    private TextView tv_offlineUserTitleCounter;

    private ServerOnlineMemberRecyclerViewAdapter serverOnlineMemberRecyclerViewAdapter;
    private ServerOfflineMemberRecyclerViewAdapter serverOfflineMemberRecyclerViewAdapter;

    public ServerMembersFragment(ModelBuilder builder) {
        this.builder = builder;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server_members, container, false);
    }

    /**
     * when view was created, after this you can get the items and start on this view
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = view.getContext();

        rv_serverMembersOnline = (RecyclerView) view.findViewById(R.id.rv_serverMembersOnline);
        tv_onlineUserTitleCounter = (TextView) view.findViewById(R.id.tv_onlineUserTitleCounter);

        rv_serverMembersOffline = (RecyclerView) view.findViewById(R.id.rv_serverMembersOffline);
        tv_offlineUserTitleCounter = (TextView) view.findViewById(R.id.tv_offlineUserTitleCounter);

        updateOnlineOfflineMemberCount();
        setupOnlineOfflineMembersRecyclerViews();
    }

    /**
     * shows all online and offline members and setup handler
     */
    private void setupOnlineOfflineMembersRecyclerViews() {
        // online member rv
        rv_serverMembersOnline.setNestedScrollingEnabled(false);
        rv_serverMembersOnline.setHasFixedSize(true);
        LinearLayoutManager layoutManagerOnlineMember = new LinearLayoutManager(context);
        serverOnlineMemberRecyclerViewAdapter = new ServerOnlineMemberRecyclerViewAdapter(context, builder);

        rv_serverMembersOnline.setLayoutManager(layoutManagerOnlineMember);
        rv_serverMembersOnline.setAdapter(serverOnlineMemberRecyclerViewAdapter);

        serverOnlineMemberRecyclerViewAdapter.setOnItemClickListener(new ServerOnlineMemberRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, User user) {
                if (user != null) {
                    onOnlineMemberClicked(user);
                }
            }

            @Override
            public void onItemLongClick(View view, User user) {
                if (user != null) {
                    onOnlineMemberLongClicked(user);
                }
            }
        });

        // offline member rv
        rv_serverMembersOffline.setNestedScrollingEnabled(false);
        rv_serverMembersOffline.setHasFixedSize(true);
        LinearLayoutManager layoutManagerOfflineMember = new LinearLayoutManager(context);
        serverOfflineMemberRecyclerViewAdapter = new ServerOfflineMemberRecyclerViewAdapter(context, builder);

        rv_serverMembersOffline.setLayoutManager(layoutManagerOfflineMember);
        rv_serverMembersOffline.setAdapter(serverOfflineMemberRecyclerViewAdapter);

        serverOfflineMemberRecyclerViewAdapter.setOnItemClickListener(new ServerOfflineMemberRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, User user) {
                if (user != null) {
                    onOfflineMemberClicked(user);
                }
            }

            @Override
            public void onItemLongClick(View view, User user) {
                if (user != null) {
                    onOfflineMemberLongClicked(user);
                }
            }
        });
    }

    /**
     * short click on online user
     */
    private void onOnlineMemberClicked(User user) {
        String userName = user.getName();
        Toast.makeText(builder.getMainActivity(), userName, Toast.LENGTH_LONG).show();
    }

    /**
     * long click on online user
     */
    private void onOnlineMemberLongClicked(User user) {
        String userId = user.getId();
        Toast.makeText(builder.getMainActivity(), userId, Toast.LENGTH_LONG).show();
    }

    /**
     * short click on online user
     */
    private void onOfflineMemberClicked(User user) {
        String userName = user.getName();
        Toast.makeText(builder.getMainActivity(), userName, Toast.LENGTH_LONG).show();
    }

    /**
     * long click on online user
     */
    private void onOfflineMemberLongClicked(User user) {
        String userId = user.getId();
        Toast.makeText(builder.getMainActivity(), userId, Toast.LENGTH_LONG).show();
    }

    /**
     * update the online member recyclerView
     */
    public void updateOnlineMembersRV() {
        // update users
        getActivity().runOnUiThread(() -> serverOnlineMemberRecyclerViewAdapter.notifyDataSetChanged());
    }

    /**
     * update the offline member recyclerView
     */
    public void updateOfflineMembersRV() {
        // update users
        getActivity().runOnUiThread(() -> serverOfflineMemberRecyclerViewAdapter.notifyDataSetChanged());
    }

    /**
     * update the textViews of the online/offline member counter above the recyclerViews
     */
    public void updateOnlineOfflineMemberCount() {
        int onlineCounter = 0;
        int offlineCounter = 0;
        for (User user : builder.getCurrentServer().getUser()) {
            if (user.isStatus()) {
                onlineCounter++;
            } else {
                offlineCounter++;
            }
        }
        int finalOnlineCounter = onlineCounter;
        getActivity().runOnUiThread(() -> tv_onlineUserTitleCounter.setText("Online - " + finalOnlineCounter));
        int finalOfflineCounter = offlineCounter;
        getActivity().runOnUiThread(() -> tv_offlineUserTitleCounter.setText("Offline - " + finalOfflineCounter));
    }
}
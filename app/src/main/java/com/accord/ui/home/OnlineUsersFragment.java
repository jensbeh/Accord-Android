package com.accord.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accord.MainActivity;
import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.adapter.rightDrawer.OnlineUserRecyclerViewAdapter;
import com.accord.model.PrivateChat;
import com.accord.model.User;

public class OnlineUsersFragment extends Fragment {
    private ModelBuilder builder;

    private RecyclerView rv_onlineUser;
    private OnlineUserRecyclerViewAdapter onlineUserRecyclerViewAdapter;
    private Context context;

    public OnlineUsersFragment(ModelBuilder builder) {
        this.builder = builder;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_online_users, container, false);
    }

    /**
     * when view was created, after this you can get the items and start on this view
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = view.getContext();

        rv_onlineUser = (RecyclerView) view.findViewById(R.id.rv_onlineUser);

        setupOnlineUsersRecyclerView();
    }

    /**
     * shows all online users and setup handler
     */
    private void setupOnlineUsersRecyclerView() {
        rv_onlineUser.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        onlineUserRecyclerViewAdapter = new OnlineUserRecyclerViewAdapter(context, builder);

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

        PrivateChat currentPrivateChat = builder.getSelectedPrivateChat();
        boolean chatExisting = false;
        String selectedUserName = user.getName();
        String selectUserId = user.getId();

        for (PrivateChat privateChat : builder.getPersonalUser().getPrivateChats()) {
            if (privateChat.getName().equals(selectedUserName)) {
                // chat existing -> show box clicked and show messages
                if (builder.getSelectedPrivateChat() == null) {
                    // but chat is not opened
                    builder.getMainActivity().showPrivateMessages();
                    builder.setState(MainActivity.State.PrivateChatView);
                } else {
                    // some chat is opened
                    builder.getPrivateMessageController().notifyOnChatChanged();
                }
                builder.setSelectedPrivateChat(privateChat);
                builder.getPrivateChatsController().updatePrivateChatsRV();
                chatExisting = true;
                break;
            }
        }

        if ((builder.getState() == MainActivity.State.HomeView && !chatExisting)) {
            // if not existing AND NO chat is opened
            builder.setState(MainActivity.State.PrivateChatView);
            builder.setSelectedPrivateChat(new PrivateChat().setName(selectedUserName).setId(selectUserId));
            builder.getPersonalUser().withPrivateChat(builder.getSelectedPrivateChat());
            chatExisting = true;
            builder.getPrivateChatsController().updatePrivateChatsRV();
            builder.getMainActivity().showPrivateMessages();
        }

        if (builder.getState() == MainActivity.State.PrivateChatView && !chatExisting) {
            // if not existing AND chat is opened
            builder.setSelectedPrivateChat(new PrivateChat().setName(selectedUserName).setId(selectUserId));
            builder.getPersonalUser().withPrivateChat(builder.getSelectedPrivateChat());
            chatExisting = true;
            builder.getPrivateChatsController().updatePrivateChatsRV();
            builder.getPrivateMessageController().notifyOnChatChanged();
        }

        builder.getMainActivity().closeRightDrawer();
    }

    /**
     * long click on online user
     */
    private void onOnlineUserLongClicked(User user) {
        String userId = user.getId();
        Toast.makeText(builder.getMainActivity(), userId, Toast.LENGTH_LONG).show();
    }

    /**
     * update the online user recyclerView
     */
    public void updateOnlineUsersRV() {
        // update users
        getActivity().runOnUiThread(() -> onlineUserRecyclerViewAdapter.notifyDataSetChanged());
    }
}
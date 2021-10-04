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
import com.accord.adapter.leftDrawer.itemContainer.PrivateChatRecyclerViewAdapter;
import com.accord.model.Channel;

public class PrivateChatsFragment extends Fragment {
    private ModelBuilder builder;

    private RecyclerView rv_privateChats;
    private PrivateChatRecyclerViewAdapter privateChatsRecyclerViewAdapter;
    private Context context;

    public PrivateChatsFragment(ModelBuilder builder) {
        this.builder = builder;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_private_chats, container, false);
    }

    /**
     * when view was created, after this you can get the items and start on this view
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = view.getContext();

        rv_privateChats = (RecyclerView) view.findViewById(R.id.rv_privateChats);

        setupPrivateChatRecyclerView();
//        updatePrivateChatsRV();
    }

    /**
     * shows all private chats and setup handler
     */
    private void setupPrivateChatRecyclerView() {
        rv_privateChats.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        privateChatsRecyclerViewAdapter = new PrivateChatRecyclerViewAdapter(context, builder);

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
        // reset notification counter
        if (selectedChannel.getUnreadMessagesCounter() > 0) {
            selectedChannel.setUnreadMessagesCounter(0);
        }

        builder.setSelectedPrivateChat(selectedChannel);
        if (builder.getState() == MainActivity.State.HomeView) {
            // if no chat is opened
            builder.setState(MainActivity.State.PrivateChatView);
            updatePrivateChatsRV();
            builder.getMainActivity().showPrivateMessages();
        } else {
            // if a chat is opened, then change
            updatePrivateChatsRV();
            builder.getPrivateMessageController().notifyOnChatChanged();
        }
        builder.getMainActivity().closeLeftDrawer();
    }

    /**
     * long click on private chat
     */
    private void onPrivateChatLongClicked(Channel chat) {
        String chatId = chat.getId();
        Toast.makeText(builder.getMainActivity(), chatId, Toast.LENGTH_LONG).show();
    }

    /**
     * update the private chat recyclerView
     */
    public void updatePrivateChatsRV() {
        getActivity().runOnUiThread(() -> privateChatsRecyclerViewAdapter.notifyDataSetChanged());
    }

    /**
     * update the private chat recyclerView
     */
    public void updateSinglePrivateChatInRV(Channel channel) {
        getActivity().runOnUiThread(() -> {
            if (builder.getPersonalUser().getPrivateChat().contains(channel)) {
                privateChatsRecyclerViewAdapter.notifyItemChanged(builder.getPersonalUser().getPrivateChat().indexOf(channel));
            }
        });
    }
}
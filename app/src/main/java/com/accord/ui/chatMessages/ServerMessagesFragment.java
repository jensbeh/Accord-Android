package com.accord.ui.chatMessages;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.adapter.mainContainer.ServerMessageRecyclerViewAdapter;
import com.accord.model.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ServerMessagesFragment extends Fragment {
    private ModelBuilder builder;
    private Context context;
    private ServerMessageRecyclerViewAdapter messageAdapter;
    private RecyclerView rv_messages;
    private CardView button_sendMessage;
    private EditText et_inputMessage;

    public ServerMessagesFragment(ModelBuilder builder) {
        this.builder = builder;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server_chat, container, false);
    }

    /**
     * when view was created, after this you can get the items and start on this view
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        context = view.getContext();

        // Setup any handles to view objects here
        rv_messages = (RecyclerView) view.findViewById(R.id.rv_server_messages);
        button_sendMessage = (CardView) view.findViewById(R.id.button_server_sendMessage);
        et_inputMessage = (EditText) view.findViewById(R.id.et_server_inputMessage);


        setupServerMessagesFragment();
    }

    private void setupServerMessagesFragment() {
        rv_messages.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        messageAdapter = new ServerMessageRecyclerViewAdapter(context, builder);
        rv_messages.setLayoutManager(layoutManager);
        rv_messages.setAdapter(messageAdapter);

        button_sendMessage.setOnClickListener(this::onSendButtonClicked);

        messageAdapter.setOnItemClickListener(new ServerMessageRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Message message) {

                String messageText = message.getMessage();
                Toast.makeText(context, messageText, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onItemLongClick(View view, Message message) {
                String messageTime = message.getCurrentTime();
                String messageFrom = message.getFrom();
                Toast.makeText(context, messageTime + " - " + messageFrom, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * add new message and scroll to bottom
     */
    public void notifyOnMessageAdded() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (builder.getCurrentServer().getCurrentServerChannel().getMessage().size() > 0) {
                    messageAdapter.notifyItemInserted(builder.getCurrentServer().getCurrentServerChannel().getMessage().size());
                    rv_messages.scrollToPosition(builder.getCurrentServer().getCurrentServerChannel().getMessage().size() - 1);
                }
            }
        });
    }

    /**
     * changes all messages after change the channel
     */
    public void notifyOnChannelChanged() {
        builder.getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (builder.getCurrentServer().getCurrentServerChannel().getMessage().size() > 0) {
                    messageAdapter.notifyItemRangeRemoved(0, builder.getCurrentServer().getCurrentServerChannel().getMessage().size());
                    messageAdapter.notifyDataSetChanged();
                    rv_messages.scrollToPosition(builder.getCurrentServer().getCurrentServerChannel().getMessage().size() - 1);
                } else {
                    messageAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * button with handler to send messages to the opposite user
     */
    private void onSendButtonClicked(View view) {
        String message = et_inputMessage.getText().toString();
        if (message.length() <= 700) {
            if (!message.equals("")) {
                if (builder.getServerChatWebSocketsMap().containsKey(builder.getCurrentServer().getId()) && builder.getCurrentServer().getCurrentServerChannel() != null) {
                    try {
                        builder.getServerChatWebSocketsMap().get(builder.getCurrentServer().getId()).sendMessage(new JSONObject().put("channel", builder.getCurrentServer().getCurrentServerChannel().getId()).put("message", message).toString());
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * method to clear the message field
     */
    public void clearMessageField() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                et_inputMessage.getEditableText().clear();
            }
        });
    }
}
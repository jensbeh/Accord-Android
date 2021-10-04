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
import com.accord.adapter.mainContainer.PrivateMessageRecyclerViewAdapter;
import com.accord.model.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class PrivateMessagesFragment extends Fragment {
    private ModelBuilder modelBuilder;
    private Context context;
    private PrivateMessageRecyclerViewAdapter messageAdapter;
    private RecyclerView rv_messages;
    private CardView button_sendMessage;
    private EditText et_inputMessage;

    public PrivateMessagesFragment(ModelBuilder modelBuilder) {
        this.modelBuilder = modelBuilder;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_private_chat_main, container, false);

    }

    /**
     * when view was created, after this you can get the items and start on this view
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        context = view.getContext();

        // Setup any handles to view objects here
        rv_messages = (RecyclerView) view.findViewById(R.id.rv_private_messages);
        button_sendMessage = (CardView) view.findViewById(R.id.button_private_sendMessage);
        et_inputMessage = (EditText) view.findViewById(R.id.et_private_inputMessage);


        setupPrivateMessagesFragment();
    }

    private void setupPrivateMessagesFragment() {
        rv_messages.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        messageAdapter = new PrivateMessageRecyclerViewAdapter(context, modelBuilder);
        rv_messages.setLayoutManager(layoutManager);
        rv_messages.setAdapter(messageAdapter);

        button_sendMessage.setOnClickListener(this::onSendButtonClicked);

        messageAdapter.setOnItemClickListener(new PrivateMessageRecyclerViewAdapter.OnItemClickListener() {
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
                if (modelBuilder.getSelectedPrivateChat().getMessage().size() > 0) {
                    messageAdapter.notifyItemInserted(modelBuilder.getSelectedPrivateChat().getMessage().size());
                    rv_messages.scrollToPosition(modelBuilder.getSelectedPrivateChat().getMessage().size() - 1);
                }
            }
        });
    }

    /**
     * changes all messages after change the chat
     */
    public void notifyOnChatChanged() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (modelBuilder.getSelectedPrivateChat().getMessage().size() > 0) {
                    messageAdapter.notifyItemRangeRemoved(0, modelBuilder.getSelectedPrivateChat().getMessage().size());
                    messageAdapter.notifyDataSetChanged();
                    rv_messages.scrollToPosition(modelBuilder.getSelectedPrivateChat().getMessage().size() - 1);
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
                try {
                    if (modelBuilder.getPrivateChatWebSocketClient() != null && modelBuilder.getSelectedPrivateChat() != null)
                        modelBuilder.getPrivateChatWebSocketClient().sendMessage(new JSONObject().put("channel", "private").put("to", modelBuilder.getSelectedPrivateChat().getName()).put("message", message).toString());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
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
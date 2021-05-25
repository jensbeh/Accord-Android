package com.accord.ui.privateChat;

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
import com.accord.adapter.MessageRecyclerViewAdapter;
import com.accord.model.Channel;
import com.accord.model.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class PrivateMessageFragment extends Fragment {
    private ModelBuilder modelBuilder;
    private Context context;
    private MessageRecyclerViewAdapter messageAdapter;
    private RecyclerView rv_messages;
    private CardView button_sendMessage;
    private EditText et_inputMessage;

    public PrivateMessageFragment(ModelBuilder modelBuilder) {
        this.modelBuilder = modelBuilder;
    }

    @Override
    public void onResume() {
        super.onResume();
        //getActivity()).afterFragmentComplete();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_private_chat, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        rv_messages = (RecyclerView) view.findViewById(R.id.rv_messages);
        button_sendMessage = (CardView) view.findViewById(R.id.button_sendMessage);
        et_inputMessage = (EditText) view.findViewById(R.id.et_inputMessage);
        rv_messages.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        messageAdapter = new MessageRecyclerViewAdapter(context, modelBuilder);
        rv_messages.setLayoutManager(layoutManager);
        rv_messages.setAdapter(messageAdapter);

        button_sendMessage.setOnClickListener(this::onSendButtonClicked);

        messageAdapter.setOnItemClickListener(new MessageRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Message message) {

                String messageText = message.getMessage();
                Toast.makeText(context, messageText, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onItemLongClick(View view, Message message) {
                String messageTime = message.getCurrentTime();
                String messageFrom = message.getFrom();
                Channel messageChannel = message.getChannel();
                Toast.makeText(context, messageTime + " - " + messageFrom + " - " + messageChannel.getName(), Toast.LENGTH_LONG).show();
            }
        });
        context = view.getContext();

        updatePrivateMessageFragment();
    }


    public void updatePrivateMessageFragment() {
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

    public void clearMessageField() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                et_inputMessage.getEditableText().clear();
            }
        });
    }
}
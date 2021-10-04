package com.accord.bottomSheets;

import static com.accord.util.Constants.CHAT_WEBSOCKET_PATH;
import static com.accord.util.Constants.SERVER_SYSTEM_WEBSOCKET_PATH;
import static com.accord.util.Constants.SERVER_WEBSOCKET_PATH;
import static com.accord.util.Constants.WS_SERVER_URL;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.accord.MainActivity;
import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.model.Server;
import com.accord.net.rest.RestClient;
import com.accord.net.rest.responses.ResponseWithJsonObject;
import com.accord.net.webSocket.chatSockets.ServerChatWebSocket;
import com.accord.net.webSocket.systemSockets.ServerSystemWebSocket;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.net.URI;

public class BottomSheetCreateServer extends BottomSheetDialog {
    private final View bottomSheetView;
    private final Context context;
    private final ModelBuilder builder;
    private Server currentServer;

    private final EditText et_serverName;
    private final Button button_create_server;

    public BottomSheetCreateServer(@NonNull Context context, int theme, ModelBuilder builder, Server currentServer) {
        super(context, theme);

        this.context = context;
        this.bottomSheetView = LayoutInflater.from(context).inflate(R.layout.sheet_create_server, findViewById(R.id.bottomSheetContainer_createServer));
        this.builder = builder;
        this.currentServer = currentServer;

        et_serverName = bottomSheetView.findViewById(R.id.et_serverName);
        button_create_server = bottomSheetView.findViewById(R.id.button_create_server);

        setupOnListener();

        this.setContentView(bottomSheetView);
    }

    private void setupOnListener() {
        button_create_server.setOnClickListener(v -> {
            createServer();
        });
    }

    private void createServer() {
        if (!et_serverName.getText().toString().isEmpty()) {
            String serverName = et_serverName.getText().toString();

            builder.getRestClient().doCreateServer(serverName, builder.getPersonalUser().getUserKey(), new RestClient.ResponseCallbackWithObject() {
                @Override
                public void onSuccess(String status, ResponseWithJsonObject.Data data) {
                    Toast.makeText(context, serverName + " SUCCESS!", Toast.LENGTH_SHORT).show();
                    String serverName = data.getName();
                    String serverId = data.getId();
                    Server server = builder.buildServer(serverName, serverId);

                    // setup new server and load everything and change view to it
                    setupServerAndViewIt(server);
                }

                @Override
                public void onFailed(Throwable error) {
                    Toast.makeText(context, "FAILED!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupServerAndViewIt(Server server) {
        builder.getMainActivity().loadServerUsers(server, new MainActivity.ServerUserCallback() {
            @Override
            public void onSuccess(String status) {
                builder.getMainActivity().loadCategories(server, new MainActivity.CategoriesLoadedCallback() {
                    @Override
                    public void onSuccess(String status) {
                        ServerSystemWebSocket serverSystemWebSocket = new ServerSystemWebSocket(builder, server, URI.create(WS_SERVER_URL + SERVER_SYSTEM_WEBSOCKET_PATH + server.getId()));
                        builder.addServerSystemWebSocket(server.getId(), serverSystemWebSocket);

                        ServerChatWebSocket serverChatWebSocket = new ServerChatWebSocket(builder, server, URI.create(WS_SERVER_URL + CHAT_WEBSOCKET_PATH + builder.getPersonalUser().getName() + SERVER_WEBSOCKET_PATH + server.getId()));
                        builder.addServerChatWebSocket(server.getId(), serverChatWebSocket);

                        closeBottomSheet();

                        builder.getMainActivity().onServerClicked(server);
                    }
                });
            }
        });
    }

    private void closeBottomSheet() {
        this.dismiss();
    }
}

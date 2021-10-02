package com.accord.bottomSheets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.model.Server;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheetCreateServer extends BottomSheetDialog {
    private final View bottomSheetView;
    private final Context context;
    private final ModelBuilder builder;
    private Server currentServer;

    private final TextView tv_createNewServer;

    public BottomSheetCreateServer(@NonNull Context context, int theme, ModelBuilder builder, Server currentServer) {
        super(context, theme);

        this.context = context;
        this.bottomSheetView = LayoutInflater.from(context).inflate(R.layout.sheet_create_server, findViewById(R.id.bottomSheetContainer_createServer));
        this.builder = builder;
        this.currentServer = currentServer;

        tv_createNewServer = bottomSheetView.findViewById(R.id.tv_createNewServer);

        setupOnListener();

        this.setContentView(bottomSheetView);
    }

    private void setupOnListener() {
        tv_createNewServer.setOnClickListener(v -> {
            Toast.makeText(context, "BlaBlaBla in server: " + currentServer.getName(), Toast.LENGTH_SHORT).show();
            closeBottomSheet();
        });
    }

    private void closeBottomSheet() {
        this.dismiss();
    }
}

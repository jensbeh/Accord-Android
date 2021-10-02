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

public class BottomSheetServerSetting extends BottomSheetDialog {
    private final View bottomSheetView;
    private final Context context;
    private final ModelBuilder builder;
    private Server currentServer;

    private final TextView tv_serverSettingsName;

    public BottomSheetServerSetting(@NonNull Context context, int theme, ModelBuilder builder, Server currentServer) {
        super(context, theme);

        this.context = context;
        this.bottomSheetView = LayoutInflater.from(context).inflate(R.layout.sheet_server_settings, findViewById(R.id.bottomSheetContainer_serverSettings));
        this.builder = builder;
        this.currentServer = currentServer;

        tv_serverSettingsName = bottomSheetView.findViewById(R.id.tv_serverSettingsName);

        setupOnListener();

        this.setContentView(bottomSheetView);
    }

    private void setupOnListener() {
        tv_serverSettingsName.setOnClickListener(v -> {
            Toast.makeText(context, "Settings from: " + currentServer.getName(), Toast.LENGTH_SHORT).show();
            closeBottomSheet();
        });
    }

    private void closeBottomSheet() {
        this.dismiss();
    }
}

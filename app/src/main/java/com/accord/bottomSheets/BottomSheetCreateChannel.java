package com.accord.bottomSheets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.model.Categories;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheetCreateChannel extends BottomSheetDialog {
    private final View bottomSheetView;
    private final Context context;
    private final ModelBuilder builder;
    private Categories currentCategory;

    private final TextView tv_createNewChannel;

    public BottomSheetCreateChannel(@NonNull Context context, int theme, ModelBuilder builder, Categories currentCategory) {
        super(context, theme);

        this.context = context;
        this.bottomSheetView = LayoutInflater.from(context).inflate(R.layout.sheet_create_channel, findViewById(R.id.bottomSheetContainer_createChannel));
        this.builder = builder;
        this.currentCategory = currentCategory;

        tv_createNewChannel = bottomSheetView.findViewById(R.id.tv_createNewChannel);

        setupOnListener();

        this.setContentView(bottomSheetView);
    }

    private void setupOnListener() {
        tv_createNewChannel.setOnClickListener(v -> {
            Toast.makeText(context, "BlaBlaBla in category: " + currentCategory.getName(), Toast.LENGTH_SHORT).show();
            closeBottomSheet();
        });
    }

    private void closeBottomSheet() {
        this.dismiss();
    }
}

package com.accord.bottomSheets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.model.Categories;
import com.accord.net.rest.RestClient;
import com.accord.net.rest.responses.ResponseWithJsonObject;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class BottomSheetCreateChannel extends BottomSheetDialog {
    private final View bottomSheetView;
    private final Context context;
    private final ModelBuilder builder;
    private Categories currentCategory;

    private final TextView tv_createNewChannel;
    private final EditText et_channelName;
    private final RadioGroup radio_group_channel_type;
    private final RadioButton radio_group_button_text;
    private final RadioButton radio_group_button_audio;
    private final Button button_create_channel;

    public BottomSheetCreateChannel(@NonNull Context context, int theme, ModelBuilder builder, Categories currentCategory) {
        super(context, theme);

        this.context = context;
        this.bottomSheetView = LayoutInflater.from(context).inflate(R.layout.sheet_create_channel, findViewById(R.id.bottomSheetContainer_createChannel));
        this.builder = builder;
        this.currentCategory = currentCategory;

        tv_createNewChannel = bottomSheetView.findViewById(R.id.tv_createNewChannel);
        et_channelName = bottomSheetView.findViewById(R.id.et_channelName);
        radio_group_channel_type = bottomSheetView.findViewById(R.id.radio_group_channel_type);
        radio_group_button_text = bottomSheetView.findViewById(R.id.radio_group_button_text);
        radio_group_button_audio = bottomSheetView.findViewById(R.id.radio_group_button_audio);
        button_create_channel = bottomSheetView.findViewById(R.id.button_create_channel);

        setupOnListener();

        this.setContentView(bottomSheetView);
    }

    private void setupOnListener() {
        button_create_channel.setOnClickListener(v -> {
            createNewChannel();
        });
    }

    private void createNewChannel() {
        if (!et_channelName.getText().toString().isEmpty()) {
            String channelName = et_channelName.getText().toString();
            String channelType = "";

            if (radio_group_button_text.isChecked()) {
                channelType = "text";
            } else {
                channelType = "audio";
            }

            builder.getRestClient().doCreateChannel(builder.getCurrentServer().getId(), currentCategory.getId(), channelName, channelType, false, builder.getPersonalUser().getUserKey(), new RestClient.ResponseCallbackWithObject() {
                @Override
                public void onSuccess(String status, ResponseWithJsonObject.Data data) {
                    closeBottomSheet();
                }

                @Override
                public void onFailed(Throwable error) {
                    Toast.makeText(context, "FAILED!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void closeBottomSheet() {
        this.dismiss();
    }
}

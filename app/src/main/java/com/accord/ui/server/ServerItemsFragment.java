package com.accord.ui.server;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accord.MainActivity;
import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.adapter.ServerCategoriesRecyclerViewAdapter;
import com.accord.bottomSheets.BottomSheetServerSetting;

public class ServerItemsFragment extends Fragment {
    private ModelBuilder builder;

    private ServerCategoriesRecyclerViewAdapter serverCategoriesRecyclerViewAdapter;
    private Context context;

    private RecyclerView rv_categories;
    private Button button_server_settings;
    private TextView tv_serverName;

    public ServerItemsFragment(ModelBuilder builder) {
        this.builder = builder;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server_items, container, false);
    }

    /**
     * when view was created, after this you can get the items and start on this view
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = view.getContext();

        rv_categories = view.findViewById(R.id.rv_categories);
        tv_serverName = view.findViewById(R.id.tv_serverName);
        button_server_settings = view.findViewById(R.id.button_server_settings);

        tv_serverName.setText(builder.getCurrentServer().getName());
        button_server_settings.setOnClickListener(this::onServerSettingsClicked);

        setupServerItemsRecyclerView();
//        updatePrivateChatsRV();
    }

    /**
     * shows all categories and setup handler
     */
    private void setupServerItemsRecyclerView() {
        rv_categories.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        serverCategoriesRecyclerViewAdapter = new ServerCategoriesRecyclerViewAdapter(context, builder);

        rv_categories.setLayoutManager(layoutManager);
        rv_categories.setAdapter(serverCategoriesRecyclerViewAdapter);
    }

    /**
     * update the private chat recyclerView and set visible or not
     */
    public void updateServerItemsRV() {
        if (builder.getState() != MainActivity.State.ServerView) {
            if (rv_categories.getVisibility() == View.INVISIBLE) {
                rv_categories.setVisibility(View.VISIBLE);
            }
            getActivity().runOnUiThread(() -> serverCategoriesRecyclerViewAdapter.notifyDataSetChanged());
        } else {
            rv_categories.setVisibility(View.INVISIBLE);
        }
    }

    private void onServerSettingsClicked(View view) {
        Toast.makeText(builder.getMainActivity(), builder.getCurrentServer().getName() + " server settings", Toast.LENGTH_LONG).show();

        // create bottomSheet for server settings with all actions
        BottomSheetServerSetting bottomSheetServerSetting = new BottomSheetServerSetting(context, R.style.BottomSheetDialogTheme, builder, builder.getCurrentServer());
        bottomSheetServerSetting.show();
    }
}
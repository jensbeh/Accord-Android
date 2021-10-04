package com.accord.ui.server;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.adapter.rightDrawer.OnlineUserRecyclerViewAdapter;
import com.accord.model.User;

public class ServerFragment extends Fragment {
    private ModelBuilder modelBuilder;
    private TextView tv_serverName;
    private RecyclerView rv_test;
    private Context context;
    private EditText et_test;
    private Button button_test;
    private OnlineUserRecyclerViewAdapter testAdapter;

    public ServerFragment(ModelBuilder modelBuilder) {
        this.modelBuilder = modelBuilder;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server, container, false);
    }

    /**
     * when view was created, after this you can get the items and start on this view
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        context = view.getContext();
        tv_serverName = getView().findViewById(R.id.tv_serverName);
        rv_test = getView().findViewById(R.id.rv_test);
        et_test = getView().findViewById(R.id.et_test);
        button_test = getView().findViewById(R.id.button_test);

        button_test.setOnClickListener(this::buttonTestClick);

        updateServerFragment();
    }

    private void buttonTestClick(View view) {

        String username = et_test.getText().toString();

        modelBuilder.buildUser(username, "Random" + modelBuilder.getPersonalUser().getUser().size(), "");
        testAdapter.notifyDataSetChanged();
    }

    /**
     * called when server is changed
     */
    public void updateServerFragment() {
        tv_serverName.setText(modelBuilder.getCurrentServer().getName());
        updateTestRecyclerView();
    }

    /**
     * test recyclerView to show currently all online users
     */
    private void updateTestRecyclerView() {
        rv_test.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        testAdapter = new OnlineUserRecyclerViewAdapter(context, modelBuilder);

        rv_test.setLayoutManager(layoutManager);
        rv_test.setAdapter(testAdapter);

        testAdapter.setOnItemClickListener(new OnlineUserRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, User user) {

                String userName = user.getName();
                Toast.makeText(context, userName, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onItemLongClick(View view, User user) {
                String userId = user.getId();
                Toast.makeText(context, userId, Toast.LENGTH_LONG).show();
            }
        });
    }
}
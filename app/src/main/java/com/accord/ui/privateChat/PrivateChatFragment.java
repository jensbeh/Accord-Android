package com.accord.ui.privateChat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.accord.ModelBuilder;
import com.accord.R;

public class PrivateChatFragment extends Fragment {
    private ModelBuilder modelBuilder;
    private Context context;
    private TextView tv_userName;

    public PrivateChatFragment(ModelBuilder modelBuilder) {
        this.modelBuilder = modelBuilder;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_private_chat, container, false);
    
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        context = view.getContext();
        tv_userName = getView().findViewById(R.id.tv_userName);

        updatePrivateChatFragment();
    }

    public void updatePrivateChatFragment() {
        tv_userName.setText(modelBuilder.getSelectedChat().getName());
        updateTestRecyclerView();
    }


    private void updateTestRecyclerView() {
        /*rv_test.setHasFixedSize(true);
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
        });*/
    }
}
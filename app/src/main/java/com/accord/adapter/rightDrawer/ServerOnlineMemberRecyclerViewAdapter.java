package com.accord.adapter.rightDrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ServerOnlineMemberRecyclerViewAdapter extends RecyclerView.Adapter<ServerOnlineMemberRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private OnItemClickListener onItemClickListener;
    private ModelBuilder modelBuilder;
    private ArrayList<User> onlineUserList;

    public interface OnItemClickListener {
        void onItemClick(View view, User user);

        void onItemLongClick(View view, User user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final TextView userName;
        private final ImageView onlineStatus;
        private boolean longClick;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            userName = (TextView) view.findViewById(R.id.tv_rv_ChatUserName);
            onlineStatus = (ImageView) view.findViewById(R.id.onlineUserStatus);
        }

        @Override
        public void onClick(View view) {
            if (longClick == false) {
                onItemClickListener.onItemClick(view, getItem(getAdapterPosition()));
            } else {
                longClick = false;
            }
        }

        @Override
        public boolean onLongClick(View view) {
            onItemClickListener.onItemLongClick(view, getItem(getAdapterPosition()));
            longClick = true;
            return false;
        }
    }

    /**
     * Initialize the data which the Adapter need.
     */
    public ServerOnlineMemberRecyclerViewAdapter(Context context, ModelBuilder modelBuilder) {
        this.context = context;
        this.modelBuilder = modelBuilder;
    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_online_user_item, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your local data at this position and replace the
        // contents of the view with that element

        onlineUserList = new ArrayList<>();
        for (User onlineUser : modelBuilder.getCurrentServer().getUser()) {
            if (onlineUser.isStatus()) {
                onlineUserList.add(onlineUser);
            }
        }

        User user = onlineUserList.get(position);
        viewHolder.userName.setText(user.getName());
        if (user.isStatus()) {
            viewHolder.onlineStatus.setImageResource(R.drawable.online_status_circle);
        }
    }

    // Return the size of your data (invoked by the layout manager)
    @Override
    public int getItemCount() {
        int counter = 0;
        for (User onlineUser : modelBuilder.getCurrentServer().getUser()) {
            if (onlineUser.isStatus()) {
                counter++;
            }
        }
        return counter;
    }

    public User getItem(int position) {
        onlineUserList.clear();
        onlineUserList = new ArrayList<>();
        for (User onlineUser : modelBuilder.getCurrentServer().getUser()) {
            if (onlineUser.isStatus()) {
                onlineUserList.add(onlineUser);
            }
        }
        return onlineUserList.get(position);
    }
}
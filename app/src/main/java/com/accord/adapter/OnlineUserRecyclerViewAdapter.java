package com.accord.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.accord.ModelBuilder;
import com.accord.R;

import org.jetbrains.annotations.NotNull;

public class OnlineUserRecyclerViewAdapter extends RecyclerView.Adapter<OnlineUserRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private OnItemClickListener onItemClickListener;
    private ModelBuilder modelBuilder;

    public interface OnItemClickListener {
        void onItemClick(int position, View view);

        void onItemLongClick(int position, View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final TextView username;
        private final ImageView onlineStatus;
        private boolean longClick;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            username = (TextView) view.findViewById(R.id.tv_rv_onlineUserName);
            onlineStatus = (ImageView) view.findViewById(R.id.onlineUserStatus);
        }

        @Override
        public void onClick(View view) {
            if (longClick == false) {
                onItemClickListener.onItemClick(getAdapterPosition(), view);
            } else {
                longClick = false;
            }
        }

        @Override
        public boolean onLongClick(View view) {
            onItemClickListener.onItemLongClick(getAdapterPosition(), view);
            longClick = true;
            return false;
        }
    }

    /**
     * Initialize the data which the Adapter need.
     */
    public OnlineUserRecyclerViewAdapter(Context context, ModelBuilder modelBuilder) {
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
        viewHolder.username.setText(modelBuilder.getPersonalUser().getUser().get(position).getName());
        viewHolder.onlineStatus.setImageResource(R.drawable.online_status_circle);
    }

    // Return the size of your data (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return modelBuilder.getPersonalUser().getUser().size();
    }
}
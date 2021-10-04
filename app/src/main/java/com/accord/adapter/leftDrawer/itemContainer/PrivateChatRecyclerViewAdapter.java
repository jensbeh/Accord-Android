package com.accord.adapter.leftDrawer.itemContainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.model.Channel;

import org.jetbrains.annotations.NotNull;

public class PrivateChatRecyclerViewAdapter extends RecyclerView.Adapter<PrivateChatRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private OnItemClickListener onItemClickListener;
    private ModelBuilder modelBuilder;

    public interface OnItemClickListener {
        void onItemClick(View view, Channel channel);

        void onItemLongClick(View view, Channel channel);
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
        private final TextView lastMessage;
        private final CardView card_view_notification;
        private final TextView tv_notification_counter;
        private final ConstraintLayout itemBackground;
        private boolean longClick;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            userName = (TextView) view.findViewById(R.id.tv_rv_userNameChats);
            lastMessage = (TextView) view.findViewById(R.id.tv_rv_lastMessageChats);
            card_view_notification = (CardView) view.findViewById(R.id.card_view_notification);
            tv_notification_counter = (TextView) view.findViewById(R.id.tv_notification_counter);
            itemBackground = (ConstraintLayout) view.findViewById(R.id.background_privateChats);
        }

        @Override
        public void onClick(View view) {
            if (!longClick) {
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
    public PrivateChatRecyclerViewAdapter(Context context, ModelBuilder modelBuilder) {
        this.context = context;
        this.modelBuilder = modelBuilder;
    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_private_chat_item, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your local data at this position and replace the
        // contents of the view with that element
        Channel channel = modelBuilder.getPersonalUser().getPrivateChat().get(position);
        viewHolder.userName.setText(channel.getName());
        if (channel.getMessage().size() > 0) {
            viewHolder.lastMessage.setText(channel.getMessage().get(channel.getMessage().size() - 1).getMessage());
        } else {
            viewHolder.lastMessage.setText("");
        }
        if (modelBuilder.getSelectedPrivateChat() != null && modelBuilder.getSelectedPrivateChat().getName().equals(channel.getName())) {
            //make privateChat background when clicked
            viewHolder.itemBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.private_chat_box_clicked_rounded_corner));
        } else {
            //reset privateChat background
            viewHolder.itemBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.privateChatNormal));
        }

        // notification icon
        if (channel.getUnreadMessagesCounter() > 0) {
            viewHolder.card_view_notification.setVisibility(View.VISIBLE);
            viewHolder.tv_notification_counter.setText(String.valueOf(channel.getUnreadMessagesCounter()));
        } else {
            viewHolder.card_view_notification.setVisibility(View.INVISIBLE);
            viewHolder.tv_notification_counter.setText("");
        }

    }

    // Return the size of your data (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return modelBuilder.getPersonalUser().getPrivateChat().size();
    }

    public Channel getItem(int position) {
        return modelBuilder.getPersonalUser().getPrivateChat().get(position);
    }
}
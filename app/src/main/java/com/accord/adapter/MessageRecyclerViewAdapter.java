package com.accord.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.model.Message;

import org.jetbrains.annotations.NotNull;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private OnItemClickListener onItemClickListener;
    private ModelBuilder modelBuilder;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public interface OnItemClickListener {
        void onItemClick(View view, Message message);

        void onItemLongClick(View view, Message message);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private boolean longClick;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
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
    public MessageRecyclerViewAdapter(Context context, ModelBuilder modelBuilder) {
        this.context = context;
        this.modelBuilder = modelBuilder;
    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_message_send, viewGroup, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_message_receive, viewGroup, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your local data at this position and replace the
        // contents of the view with that element
        Message message = modelBuilder.getSelectedPrivateChat().getMessage().get(position);

        switch (viewHolder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) viewHolder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) viewHolder).bind(message);
        }

    }

    @Override
    public int getItemViewType(int position) {
        Message message = modelBuilder.getSelectedPrivateChat().getMessage().get(position);

        if (message.getFrom().equals(modelBuilder.getPersonalUser().getName())) {
            // if currentUser send
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // if currentUser received
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Return the size of your data (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return modelBuilder.getSelectedPrivateChat().getMessage().size();
    }

    public Message getItem(int position) {
        return modelBuilder.getSelectedPrivateChat().getMessage().get(position);
    }


    private class ReceivedMessageHolder extends ViewHolder {
        TextView messageText;
        TextView timestamp;

        ReceivedMessageHolder(View view) {
            super(view);
            messageText = (TextView) view.findViewById(R.id.tv_message_receive);
            timestamp = (TextView) view.findViewById(R.id.tv_timestamp_receive);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            timestamp.setText(message.getCurrentTime());
        }
    }

    private class SentMessageHolder extends ViewHolder {
        TextView messageText;
        TextView timestamp;

        SentMessageHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.tv_message_send);
            timestamp = (TextView) view.findViewById(R.id.tv_timestamp_send);
        }

        void bind(Message message) {
            String str = message.getMessage();
            messageText.setText(str);
            timestamp.setText(message.getCurrentTime());
        }
    }
}
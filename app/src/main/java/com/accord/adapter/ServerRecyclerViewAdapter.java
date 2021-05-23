package com.accord.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.model.Server;

import org.jetbrains.annotations.NotNull;

public class ServerRecyclerViewAdapter extends RecyclerView.Adapter<ServerRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private OnItemClickListener onItemClickListener;
    private ModelBuilder modelBuilder;

    public interface OnItemClickListener {
        void onItemClick(View view, Server server);

        void onItemLongClick(View view, Server server);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private final TextView serverName;
        private final CardView serverCard;
        private boolean longClick;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            serverCard = (CardView) view.findViewById(R.id.card_view_server);
            serverName = (TextView) view.findViewById(R.id.tv_serverName);
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
    public ServerRecyclerViewAdapter(Context context, ModelBuilder modelBuilder) {
        this.context = context;
        this.modelBuilder = modelBuilder;
    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_server_item, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your local data at this position and replace the
        // contents of the view with that element
        viewHolder.serverName.setText(modelBuilder.getPersonalUser().getServer().get(position).getName());
        //viewHolder.onlineStatus.setImageResource(R.drawable.online_status_circle);

        if (modelBuilder.getCurrentServer() == modelBuilder.getPersonalUser().getServer().get(position)) {
            viewHolder.serverCard.setCardBackgroundColor(Color.BLUE);
        } else {
            viewHolder.serverCard.setCardBackgroundColor(Color.RED);
        }

    }

    // Return the size of your data (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return modelBuilder.getPersonalUser().getServer().size();
    }

    public Server getItem(int position) {
        return modelBuilder.getPersonalUser().getServer().get(position);
    }
}
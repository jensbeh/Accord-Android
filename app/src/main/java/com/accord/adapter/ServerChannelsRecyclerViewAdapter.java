package com.accord.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.model.Categories;
import com.accord.model.Channel;
import com.accord.model.ServerChannel;

import org.jetbrains.annotations.NotNull;

public class ServerChannelsRecyclerViewAdapter extends RecyclerView.Adapter<ServerChannelsRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ModelBuilder modelBuilder;
    private final Categories currentCategory;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView channelName;
        private final LinearLayout ll_clickArea_channel;

        public ViewHolder(View view) {
            super(view);

            channelName = (TextView) view.findViewById(R.id.tv_rv_channelTypeAndName);
            ll_clickArea_channel = (LinearLayout) view.findViewById(R.id.ll_clickArea_channel);
        }
    }

    /**
     * Initialize the data which the Adapter need.
     */
    public ServerChannelsRecyclerViewAdapter(Context context, ModelBuilder modelBuilder, Categories currentCategory) {
        this.context = context;
        this.modelBuilder = modelBuilder;
        this.currentCategory = currentCategory;
    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_server_channel_item, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your local data at this position and replace the
        // contents of the view with that element
        ServerChannel serverChannel = currentCategory.getChannel().get(position);

        if (serverChannel.getType().equals("text")) {
            viewHolder.channelName.setText("\uD83D\uDD8A " + serverChannel.getName());
        } else if (serverChannel.getType().equals("audio")) {
            viewHolder.channelName.setText("\uD83D\uDD0A " + serverChannel.getName());
        }

        // listener for channel name clicked
        viewHolder.ll_clickArea_channel.setOnClickListener(v -> onServerChannelClicked(serverChannel));
        viewHolder.ll_clickArea_channel.setOnLongClickListener(v -> {
            onServerChannelLongClicked(serverChannel);
            return true;
        });
    }

    // Return the size of your data (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return currentCategory.getChannel().size();
    }

    public ServerChannel getItem(int position) {
        return currentCategory.getChannel().get(position);
    }

    private void onServerChannelClicked(Channel selectedServerChannel) {
        Toast.makeText(context, selectedServerChannel.getName(), Toast.LENGTH_LONG).show();
    }

    private void onServerChannelLongClicked(Channel selectedServerChannel) {
        Toast.makeText(context, selectedServerChannel.getId(), Toast.LENGTH_LONG).show();
    }
}
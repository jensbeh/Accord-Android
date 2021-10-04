package com.accord.adapter.leftDrawer.itemContainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.model.Categories;
import com.accord.model.ServerChannel;

import org.jetbrains.annotations.NotNull;

public class ServerChannelsRecyclerViewAdapter extends RecyclerView.Adapter<ServerChannelsRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ModelBuilder builder;
    private final Categories currentCategory;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView channelName;
        private final LinearLayout ll_clickArea_channel;
        private final ConstraintLayout background_serverChannel;

        public ViewHolder(View view) {
            super(view);

            channelName = (TextView) view.findViewById(R.id.tv_rv_channelTypeAndName);
            ll_clickArea_channel = (LinearLayout) view.findViewById(R.id.ll_clickArea_channel);
            background_serverChannel = (ConstraintLayout) view.findViewById(R.id.background_serverChannel);
        }
    }

    /**
     * Initialize the data which the Adapter need.
     */
    public ServerChannelsRecyclerViewAdapter(Context context, ModelBuilder builder, Categories currentCategory) {
        this.context = context;
        this.builder = builder;
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

        if (builder.getCurrentServer().getCurrentServerChannel() != null && builder.getCurrentServer().getCurrentServerChannel().getId().equals(serverChannel.getId())) {
            // this channel is the currentChannel
            // select color
            viewHolder.background_serverChannel.setBackground(ContextCompat.getDrawable(context, R.drawable.server_channel_box_clicked_rounded_corner));
        } else {
            // not selected color
            viewHolder.background_serverChannel.setBackgroundColor(ContextCompat.getColor(context, R.color.serverChannelNormal));
        }
    }

    // Return the size of your data (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return currentCategory.getChannel().size();
    }

    public ServerChannel getItem(int position) {
        return currentCategory.getChannel().get(position);
    }

    private void onServerChannelClicked(ServerChannel selectedServerChannel) {
        Toast.makeText(context, selectedServerChannel.getName(), Toast.LENGTH_LONG).show();


        //if NEW selected channel change background color and set new selected channel
        ServerChannel oldSelectedOne = builder.getCurrentServer().getCurrentServerChannel();
        if (oldSelectedOne == null || !oldSelectedOne.getId().equals(selectedServerChannel.getId())) {
            if (selectedServerChannel.getType().equals("text")) {

                if (selectedServerChannel.getUnreadMessagesCounter() > 0) {
                    // reset unreadMessageCounter and make it invisible
                    selectedServerChannel.setUnreadMessagesCounter(0);

//                    this.notifyItemChanged(currentCategory.getChannel().indexOf(selectedServerChannel));
                }

                builder.getCurrentServer().setCurrentServerChannel(selectedServerChannel);

                if (oldSelectedOne != null && !currentCategory.getChannel().contains(oldSelectedOne)) {
                    // from another category
                    for (Categories category : builder.getCurrentServer().getCategories()) {
                        if (category.getChannel().contains(oldSelectedOne)) {
                            builder.getChannelAdapterMap().get(category.getId()).notifyItemChanged(category.getChannel().indexOf(oldSelectedOne));
                            break;
                        }
                    }
                } else {
                    this.notifyItemChanged(currentCategory.getChannel().indexOf(oldSelectedOne));
                }
                this.notifyItemChanged(currentCategory.getChannel().indexOf(selectedServerChannel));

                // show messages of the channel
                if (oldSelectedOne == null) {
                    builder.getMainActivity().showServerMessages();
                } else {
                    builder.getServerMessageController().notifyOnChannelChanged();
                }

                // close left drawer after selection new channel
                builder.getMainActivity().closeLeftDrawer();
            }
        }

    }

    private void onServerChannelLongClicked(ServerChannel selectedServerChannel) {
        Toast.makeText(context, selectedServerChannel.getId(), Toast.LENGTH_LONG).show();
    }
}
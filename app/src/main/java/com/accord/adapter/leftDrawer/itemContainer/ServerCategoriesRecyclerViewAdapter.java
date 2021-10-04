package com.accord.adapter.leftDrawer.itemContainer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.accord.ModelBuilder;
import com.accord.R;
import com.accord.bottomSheets.BottomSheetCreateChannel;
import com.accord.model.Categories;

import org.jetbrains.annotations.NotNull;

public class ServerCategoriesRecyclerViewAdapter extends RecyclerView.Adapter<ServerCategoriesRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ModelBuilder builder;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryName;
        private final Button createChannelButton;
        private final LinearLayout ll_clickArea_category;
        private final RecyclerView rv_channel;

        public ViewHolder(View view) {
            super(view);

            categoryName = (TextView) view.findViewById(R.id.tv_rv_categoryName);
            createChannelButton = (Button) view.findViewById(R.id.button_create_channel_plus);
            ll_clickArea_category = (LinearLayout) view.findViewById(R.id.ll_clickArea_category);
            rv_channel = (RecyclerView) view.findViewById(R.id.rv_channel);
        }
    }

    /**
     * Initialize the data which the Adapter need.
     */
    public ServerCategoriesRecyclerViewAdapter(Context context, ModelBuilder builder) {
        this.context = context;
        this.builder = builder;
    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.raw_server_category_item, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your local data at this position and replace the
        // contents of the view with that element
        Categories category = builder.getCurrentServer().getCategories().get(position);
        viewHolder.categoryName.setText(category.getName());

        if (builder.getCurrentServer().getOwner().equals(builder.getPersonalUser().getId())) {
            // serverOwner action
            viewHolder.createChannelButton.setVisibility(View.VISIBLE);
            viewHolder.createChannelButton.setOnClickListener(v -> onCreateChannelClicked(category));
        } else {
            // not serverOwner action
            viewHolder.createChannelButton.setVisibility(View.INVISIBLE);
        }

        // listener for category name clicked
        viewHolder.ll_clickArea_category.setOnClickListener(v -> onCategoryClicked(category, viewHolder));
        viewHolder.ll_clickArea_category.setOnLongClickListener(v -> {
            onCategoryLongClicked(category);
            return true;
        });

        // setup adapter for channels rv
        viewHolder.rv_channel.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        ServerChannelsRecyclerViewAdapter serverChannelsRecyclerViewAdapter = new ServerChannelsRecyclerViewAdapter(context, builder, category);

        viewHolder.rv_channel.setLayoutManager(layoutManager);
        viewHolder.rv_channel.setAdapter(serverChannelsRecyclerViewAdapter);

        // map to refresh the items between the categories
        // every time the category is displayed the new adapter is set / will be overwritten if existing (important, else there can't be updated items)
        builder.getChannelAdapterMap().put(category.getId(), serverChannelsRecyclerViewAdapter);
    }

    // Return the size of your data (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return builder.getCurrentServer().getCategories().size();
    }

    public Categories getItem(int position) {
        return builder.getCurrentServer().getCategories().get(position);
    }

    private void onCreateChannelClicked(Categories category) {
        Toast.makeText(context, "create channel in " + category.getName(), Toast.LENGTH_SHORT).show();

        // create bottomSheet for create channel with all actions
        BottomSheetCreateChannel bottomSheetCreateChannel = new BottomSheetCreateChannel(context, R.style.BottomSheetDialogTheme, builder, category);
        bottomSheetCreateChannel.show();
    }

    /**
     * short click on category name
     */
    private void onCategoryClicked(Categories selectedCategory, ViewHolder viewHolder) {
        Toast.makeText(context, selectedCategory.getName(), Toast.LENGTH_LONG).show();

        viewHolder.rv_channel.setVisibility(viewHolder.rv_channel.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    /**
     * long click on category name
     */
    private void onCategoryLongClicked(Categories selectedCategory) {
        Toast.makeText(builder.getMainActivity(), selectedCategory.getId(), Toast.LENGTH_LONG).show();
    }
}
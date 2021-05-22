package com.accord.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.accord.MainActivity;
import com.accord.R;
import com.accord.model.User;

import java.util.List;

public class OnlineUserAdapter extends BaseAdapter {

    Context context;
    List<User> onlineUser;

    public OnlineUserAdapter(Context context, List<User> onlineUser) {
        this.context = context;
        this.onlineUser = onlineUser;
    }

    @Override
    public int getCount() {
        return onlineUser.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);

        TextView username = convertView.findViewById(R.id.onlineUserName);

        username.setText(MainActivity.getModelBuilder().getPersonalUser().getUser().get(position).getName());

        return convertView;
    }
}

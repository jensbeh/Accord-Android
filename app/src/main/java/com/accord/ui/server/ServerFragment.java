package com.accord.ui.server;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.accord.ModelBuilder;
import com.accord.R;

public class ServerFragment extends Fragment {
    private ModelBuilder modelBuilder;

    public ServerFragment(ModelBuilder modelBuilder) {
        this.modelBuilder = modelBuilder;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_server, container, false);
    }
}
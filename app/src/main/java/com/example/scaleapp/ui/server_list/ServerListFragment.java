package com.example.scaleapp.ui.server_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.scaleapp.Model.ConnectionManager;
import com.example.scaleapp.R;


import java.util.ArrayList;

public class ServerListFragment extends Fragment {
    private ServerListViewModel serverListViewModel;
    private ArrayList<Item> itemList = new ArrayList();

    private ListView online_list;
    ServerListAdapter customAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        serverListViewModel =
                new ViewModelProvider(this).get(ServerListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_server_list, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);

        online_list = root.findViewById(R.id.server_list);


        serverListViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        downloadData();
        return root;
    }

    private void downloadData() {
        ConnectionManager connectionManager = new ConnectionManager();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                itemList = connectionManager.getItemList();
            }
        });
        thread.start();

        do {

        } while (thread.isAlive());
        setItemList();
    }

    private void setItemList() {

        customAdapter = new ServerListAdapter(itemList, getContext());
        online_list.setAdapter(customAdapter);
        online_list.setClickable(false);
        customAdapter.notifyDataSetChanged();
    }
}
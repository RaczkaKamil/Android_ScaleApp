package com.example.scaleapp.ui.server_list;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.scaleapp.R;

import java.util.ArrayList;

public class ServerListAdapter extends ArrayAdapter<Item> {
    public ServerListAdapter(ArrayList<Item> data, Context context) {
        super(context, R.layout.server_list_view, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item dataModel = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.server_list_view, parent, false);
            viewHolder.itemID = convertView.findViewById(R.id.itemID);
            viewHolder.itemName = convertView.findViewById(R.id.itemName);
            viewHolder.itemValue = convertView.findViewById(R.id.itemValue);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        try {
            assert dataModel != null;
            viewHolder.itemID.setText(dataModel.getProduct_id());
            viewHolder.itemName.setText(dataModel.getProduct_name());
            viewHolder.itemValue.setText(dataModel.getProduct_weight());
        } catch (ArrayIndexOutOfBoundsException e) {
            e.fillInStackTrace();
        } catch (NullPointerException e) {
            e.fillInStackTrace();
        }

        return convertView;
    }


    private static class ViewHolder {
        TextView itemID;
        TextView itemName;
        TextView itemValue;

    }

}
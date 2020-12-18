package com.example.scaleapp.ui.ScaleSearcher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.scaleapp.R;

import java.util.ArrayList;

public class ScaleListAdapter extends ArrayAdapter<Scale> {
    public ScaleListAdapter(ArrayList<Scale> data, Context context) {
        super(context, R.layout.scale_list_view, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Scale dataModel = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.scale_list_view, parent, false);
            viewHolder.scaleName = convertView.findViewById(R.id.scaleName);
            viewHolder.scaleID = convertView.findViewById(R.id.scaleID);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        try {
            assert dataModel != null;
            viewHolder.scaleName.setText(dataModel.getName());
            viewHolder.scaleID.setText(dataModel.getID());
        } catch (ArrayIndexOutOfBoundsException e) {
            e.fillInStackTrace();
        } catch (NullPointerException e) {
            e.fillInStackTrace();
        }

        return convertView;
    }


    private static class ViewHolder {
        TextView scaleName;
        TextView scaleID;
    }

}
package com.example.tailor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mData;

    // Constructor
    public CustomAdapter(Context context, ArrayList<String> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Inflate the layout for each list row
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_layout, parent, false);

            // Create a ViewHolder to store reference to the views
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.textView);

            // Set tag on convertView to hold the ViewHolder
            convertView.setTag(holder);
        } else {
            // ViewHolder already exists, retrieve it from the tag
            holder = (ViewHolder) convertView.getTag();
        }

        // Set the data to the views
        holder.textView.setText(mData.get(position));

        return convertView;
    }

    // ViewHolder class to hold references to views for recycling
    static class ViewHolder {
        TextView textView;
    }
}

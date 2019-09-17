package com.dawn.newlandscan.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.dawn.newlandscan.R;

import java.util.List;

public class MenuItemAdapter extends BaseAdapter {
    public static final String TAG = MenuItemAdapter.class.getCanonicalName();
    private Context mContext;
    private List<MenuItem> mList;

    public MenuItemAdapter(Context context, List<MenuItem> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        if (this.mList == null)
            return 0;
        return this.mList.size();
    }

    @Override
    public Object getItem(int position) {
        if (this.mList == null)
            return null;
        return this.mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position >= this.mList.size())
            return convertView;
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.menu_item, null, false);
            holder = new ViewHolder();
            holder.mIconImageView = (ImageView) convertView
                    .findViewById(R.id.menuItemImageView);
            holder.mNameTextView = (TextView) convertView
                    .findViewById(R.id.menuItemNameTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MenuItem item = mList.get(position);
        holder.mIconImageView.setImageResource(item.getIconID());
        holder.mNameTextView.setText(item.getName());
        return convertView;
    }

    private static class ViewHolder {
        ImageView mIconImageView;
        TextView mNameTextView;
    }
}


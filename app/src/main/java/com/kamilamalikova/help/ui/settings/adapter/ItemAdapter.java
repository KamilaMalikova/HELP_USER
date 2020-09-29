package com.kamilamalikova.help.ui.settings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kamilamalikova.help.R;

public class ItemAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    String[] items;
    String[] index;

    public ItemAdapter(Context c, String[] index, String[] items){
        this.index = index;
        this.items = items;
        this.mInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.list_detail, null);
        TextView indexTextView = v.findViewById(R.id.itemIndexTextView);
        TextView itemNameTextView = v.findViewById(R.id.itemNameTextView);

        String item = this.items[position];
        String index = this.index[position];

        indexTextView.setText(index);
        itemNameTextView.setText(item);

        return v;
    }
}

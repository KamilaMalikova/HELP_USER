package com.kamilamalikova.help.ui.stock.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.kamilamalikova.help.R;

public class DocTypeAdapter extends BaseAdapter {

    String id[];
    String type[];
    LayoutInflater mInflater;
    int layoutRes;

    public DocTypeAdapter(String[] id, String[] type, Context context, int layoutRes) {
        this.id = id;
        this.type = type;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutRes = layoutRes;
    }

    @Override
    public int getCount() {
        return id.length;
    }

    @Override
    public Object getItem(int position) {
        return new DocTypeObject(id[position], type[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View v = this.mInflater.inflate(this.layoutRes, null);
        TextView indexTextView = v.findViewById(R.id.spinItemIndexTextView);
        TextView itemNameTextView = v.findViewById(R.id.spinItemNameTextView);
        String index = this.id[position];
        String item = this.type[position];
        indexTextView.setText(index);
        itemNameTextView.setText(item);

        return v;
    }
}

package com.kamilamalikova.help.ui.terminal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.TableType;

import java.util.ArrayList;
import java.util.List;

public class TableFilterAdapter extends BaseAdapter {
    List<TableType> types;
    LayoutInflater mInflater;

    public TableFilterAdapter(Context context) {
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.types = new ArrayList<>();
        types.add(TableType.ALL);
        types.add(TableType.RESERVED);
        types.add(TableType.FREE);
    }

    @Override
    public int getCount() {
        return types.size();
    }

    @Override
    public Object getItem(int position) {
        return types.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = mInflater.inflate(R.layout.spin_item, null);
        TextView spinItemNameTextView = convertView.findViewById(R.id.spinItemNameTextView);
        spinItemNameTextView.setText(types.get(position).getRu_name());
        return convertView;
    }
}

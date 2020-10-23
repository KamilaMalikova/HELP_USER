package com.kamilamalikova.help.ui.history.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.OrderStatus;

import java.util.ArrayList;
import java.util.List;

public class OrderStatusAdapter extends BaseAdapter {
    List<OrderStatus> statuses;
    LayoutInflater inflater;

    public OrderStatusAdapter(Context context){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        statuses = new ArrayList<>();
        statuses.add(OrderStatus.ALL);
        statuses.add(OrderStatus.CREATED);
        statuses.add(OrderStatus.CLOSED);
    }

    @Override
    public int getCount() {
        return statuses.size();
    }

    @Override
    public Object getItem(int position) {
        return statuses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = inflater.inflate(R.layout.spin_item, null);

        TextView statusTextView = convertView.findViewById(R.id.spinItemNameTextView);
        statusTextView.setText(statuses.get(position).getRu_name());
        return convertView;
    }
}

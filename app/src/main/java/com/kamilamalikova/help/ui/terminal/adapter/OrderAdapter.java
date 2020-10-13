package com.kamilamalikova.help.ui.terminal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.Order;
import com.kamilamalikova.help.model.OrderDetail;
import com.kamilamalikova.help.model.Product;

public class OrderAdapter extends BaseAdapter {
    Order order;
    LayoutInflater mInflater;

    public OrderAdapter(Context context, Order order) {
        this.order = order;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return order.getOrderDetails().size();
    }

    @Override
    public Object getItem(int position) {
        return order.getOrderDetails().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.menu_product_item_layout, null);
        }

        TextView menuItemId = convertView.findViewById(R.id.menuItemId);
        ImageButton addFromMenuBtn = convertView.findViewById(R.id.addFromMenuBtn);
        TextView menuItemNameTextView = convertView.findViewById(R.id.menuItemNameTextView);
        TextView menuItemQtyTextView = convertView.findViewById(R.id.menuItemQtyTextView);
        TextView menuItemCostTextView = convertView.findViewById(R.id.menuItemCostTextView);
        TextView menuItemSelectedQtyTextView = convertView.findViewById(R.id.menuItemSelectedQtyTextView);
        addFromMenuBtn.setImageResource(R.drawable.ic_baseline_edit_24);
        menuItemQtyTextView.setVisibility(View.INVISIBLE);

        OrderDetail orderDetail = order.getOrderDetails().get(position);

        menuItemId.setText((orderDetail.getId()+""));
        menuItemNameTextView.setText((orderDetail.getProduct().getProductName()+""));
        menuItemCostTextView.setText(("Цена: "+": "+orderDetail.getCost()));
        menuItemSelectedQtyTextView.setText((orderDetail.getQuantity()+""));

        return convertView;
    }
}

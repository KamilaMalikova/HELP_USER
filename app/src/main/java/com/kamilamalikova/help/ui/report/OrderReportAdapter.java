package com.kamilamalikova.help.ui.report;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.OrderDetail;

import org.w3c.dom.Text;

import java.util.List;

public class OrderReportAdapter extends BaseAdapter {
    List<OrderDetail> orderDetails;
    LayoutInflater mInflater;
    Context context;

    public OrderReportAdapter(Context context, List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public int getCount() {
        return orderDetails.size();
    }

    @Override
    public Object getItem(int position) {
        return orderDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = mInflater.inflate(R.layout.order_item, null);
        TextView productNameTextView = convertView.findViewById(R.id.orderProductItemNameTextView);
        TextView productQtyTextView = convertView.findViewById(R.id.orderProductItemQtyTextView);
        TextView productCostTextView = convertView.findViewById(R.id.orderProductItemCostTextView);
        OrderDetail detail = orderDetails.get(position);
        productNameTextView.setText(detail.getProduct().getProductName());
        productQtyTextView.setText((context.getText(R.string.qty)+": "+detail.getQuantity()));
        productCostTextView.setText((context.getText(R.string.cost)+": "+detail.getCost()));
        return convertView;
    }
}

package com.kamilamalikova.help.ui.history.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.Order;
import com.kamilamalikova.help.model.OrderDetail;
import com.kamilamalikova.help.model.StockInventory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderHistoryAdapter extends BaseExpandableListAdapter {
    List<Order> orders;
    Context context;
    LayoutInflater mInflater;

    private static final int LOADING = 0;
    private static final int ITEM = 1;
    private boolean isLoadingAdded = false;

    public OrderHistoryAdapter(Context context, List<Order> orders) {
        this.orders = orders;
        this.context = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public OrderHistoryAdapter(Context context) {
        this.context = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.orders = new ArrayList<>();
    }

    public void init(){
        this.orders = new ArrayList<>();
    }

    public void add(JSONArray responseArray) throws JSONException {
        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject jsonObject = responseArray.getJSONObject(i);
            orders.add(new Order(jsonObject));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return orders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return orders.get(groupPosition).getOrderDetails().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return orders.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return orders.get(groupPosition).getOrderDetails().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = mInflater.inflate(R.layout.order_history_item, null);

        TextView orderItemDocIdTextView = convertView.findViewById(R.id.orderItemDocIdTextView);
        TextView orderCreatedDateTextView = convertView.findViewById(R.id.orderCreatedDateTextView);
        TextView orderWaiterNameTextView = convertView.findViewById(R.id.orderWaiterNameTextView);
        TextView orderSumTextView = convertView.findViewById(R.id.OrderSumTextView);

        Order order = orders.get(groupPosition);

        orderItemDocIdTextView.setText((order.getOrderId()+""));
        LocalDateTime date = order.getDateTime();
        orderCreatedDateTextView.setText((date.getDayOfMonth()+"."+date.getMonthValue()+"."+date.getYear()));
        orderWaiterNameTextView.setText(order.getWaiterName());
        orderSumTextView.setText((order.getSum()+" "+context.getString(R.string.uz_sum)));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = mInflater.inflate(R.layout.product_item, null);

        TextView indexTextView = convertView.findViewById(R.id.productIdItemTextView);
        TextView itemNameTextView = convertView.findViewById(R.id.productNameItemTextView);
        TextView itemQtyTextView = convertView.findViewById(R.id.productQtyItemTextView);
        TextView itemUnitTextView = convertView.findViewById(R.id.qtyName);

        OrderDetail orderDetail = orders.get(groupPosition).getOrderDetails().get(childPosition);

        indexTextView.setText(((childPosition+1)+""));
        itemNameTextView.setText(orderDetail.getProduct().getProductName());
        itemQtyTextView.setText((orderDetail.getQuantity()+""));
        itemUnitTextView.setText(orderDetail.getProduct().getUnit().getUnitName());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
    }
}

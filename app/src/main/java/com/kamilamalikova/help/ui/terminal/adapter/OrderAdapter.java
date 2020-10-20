package com.kamilamalikova.help.ui.terminal.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.Order;
import com.kamilamalikova.help.model.OrderDetail;
import com.kamilamalikova.help.model.Product;
import com.kamilamalikova.help.ui.terminal.fragments.OrderFragment;

import java.util.LinkedHashSet;

public class OrderAdapter extends BaseAdapter {
    Order order;
    LayoutInflater mInflater;
    OrderFragment fragment;

    public OrderAdapter(Context context, Order order, OrderFragment fragment) {
        this.order = order;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fragment = fragment;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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
        ImageButton minusFromMenuBtn = convertView.findViewById(R.id.minusFromMenuBtn);
        TextView menuItemNameTextView = convertView.findViewById(R.id.menuItemNameTextView);
        //TextView menuItemQtyTextView = convertView.findViewById(R.id.menuItemQtyTextView);
        TextView menuItemCostTextView = convertView.findViewById(R.id.menuItemCostTextView);
       final TextView menuItemSelectedQtyTextView = convertView.findViewById(R.id.menuItemSelectedQtyTextView);

        final OrderDetail orderDetail = order.getOrderDetails().get(position);

        menuItemId.setText((orderDetail.getId()+""));
        menuItemNameTextView.setText((orderDetail.getProduct().getProductName()+""));
        menuItemCostTextView.setText(("Цена: "+": "+orderDetail.getCost()));
        menuItemSelectedQtyTextView.setText((orderDetail.getQuantity()+""));

        menuItemSelectedQtyTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                double qty = (s.toString().isEmpty()) ? 0.0 : Double.parseDouble(s.toString());
                if (qty < 0.0){
                    Toast.makeText(fragment.getContext(), "Значение не может быть отрицательным", Toast.LENGTH_LONG)
                            .show();
                    return;
                } else {

                    double sum = 0;
                    for (OrderDetail subOrder: order.getOrderDetails()) {
                        sum+=subOrder.getQuantity();
                    }
                    if (qty < 0.0 || (sum+(qty-orderDetail.getQuantity())) < 0){
                        Toast.makeText(fragment.getContext(), "Вы пытаетесь отменить больше чем заказали", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }

                    if (fragment.newOrderDetails == null){
                        fragment.newOrderDetails = new LinkedHashSet<>();
                    }

                    fragment.openMenuBtn.setText("Обновить заказ");
                    Product product = orderDetail.getProduct();
                    product.setBuyQty(qty-orderDetail.getQuantity());
                    fragment.newOrderDetails.add(product);

                }
            }
        });

        addFromMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment.newOrderDetails == null){
                    fragment.newOrderDetails = new LinkedHashSet<>();
                }
                double qty = Double.parseDouble(menuItemSelectedQtyTextView.getText().toString());
                qty+=1;
                double sum = 0;
                for (OrderDetail subOrder: order.getOrderDetails()) {
                    sum+=subOrder.getQuantity();
                }
                if (qty < 0.0 || (sum+(qty-orderDetail.getQuantity())) < 0){
                    return;
                }
                menuItemSelectedQtyTextView.setText((qty+""));
            }
        });

        minusFromMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragment.newOrderDetails == null){
                    fragment.newOrderDetails = new LinkedHashSet<>();
                }
                double qty = Double.parseDouble(menuItemSelectedQtyTextView.getText().toString());
                qty-=1;
                double sum = 0;
                for (OrderDetail subOrder: order.getOrderDetails()) {
                    sum+=subOrder.getQuantity();
                }
                Log.i("--", Double.toString(sum+(qty-orderDetail.getQuantity())));
                Log.i("--", Double.toString(sum+(qty-orderDetail.getQuantity())));
                if (qty < 0.0 || (sum+(qty-orderDetail.getQuantity())) < 0){
                    return;
                }
                menuItemSelectedQtyTextView.setText((qty+""));

            }
        });

        return convertView;
    }
}
package com.kamilamalikova.help.ui.stock.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.Product;
import com.kamilamalikova.help.model.StockItemBalance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ProductItemAdapter extends BaseAdapter {

    List<ProductItemObject> productList;
    LayoutInflater mInflater;


    public ProductItemAdapter(Context context, List<ProductItemObject> productItemObjectList){
        this.productList = productItemObjectList;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ProductItemAdapter(Context context, JSONArray jsonArray) throws JSONException {

        productList = new ArrayList<>();

        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            productList.add(new ProductItemObject(
                                false,
                                         new StockItemBalance(object),
                                         0.0));
        }
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View view = this.mInflater.inflate(R.layout.stock_product_item, null);

        final CheckBox isChosenCheckBox = view.findViewById(R.id.stockDocProductCheckBox);
        TextView stockDocProductNameTextView = view.findViewById(R.id.stockDocProductNameTextView);
        final EditText stockDocQtyTextView = view.findViewById(R.id.stockDocQtyTextView);
        TextView stockDocIdTextView = view.findViewById(R.id.stockDocIdTextView);
        TextView stockItemQtyTextView = view.findViewById(R.id.stockItemQtyTextView);
        ImageButton addBtn = view.findViewById(R.id.addBtn);
        ImageButton minusBtn = view.findViewById(R.id.minusBtn);

        isChosenCheckBox.setChecked(productList.get(position).isChosen());
        stockDocProductNameTextView.setText(productList.get(position).getProduct().getName());
        stockDocQtyTextView.setText((Double.toString(productList.get(position).getQty())));
        stockDocIdTextView.setText((Long.toString(productList.get(position).getProduct().getId())));
        stockItemQtyTextView.setText(
                ("В наличии: "
                        +": "
                        +(productList.get(position).getProduct().getInStockQty())));


        stockDocQtyTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                double qty = (s.toString().isEmpty()) ? 0.0 : Double.parseDouble(s.toString());
                if (qty <= 0.0){
                    isChosenCheckBox.setChecked(false);
                    productList.get(position).setChosen(false);
                    productList.get(position).setQty(0.0);
                    return;
                }
                isChosenCheckBox.setChecked(true);
                productList.get(position).setChosen(true);
                productList.get(position).setQty(qty);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double qty = (stockDocQtyTextView.getText().toString().isEmpty()) ? 0.0 : Double.parseDouble(stockDocQtyTextView.getText().toString());
                qty+=1;
                stockDocQtyTextView.setText((qty+""));
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double qty = (stockDocQtyTextView.getText().toString().isEmpty()) ? 0.0 : Double.parseDouble(stockDocQtyTextView.getText().toString());
                qty-=1;
                stockDocQtyTextView.setText((qty+""));
            }
        });
        return view;
    }
}

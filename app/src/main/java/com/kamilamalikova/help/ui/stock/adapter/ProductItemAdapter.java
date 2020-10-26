package com.kamilamalikova.help.ui.stock.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.navigation.Navigation;
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

    public List<ProductItemObject> productList;
    LayoutInflater mInflater;
    boolean out;
    Spinner docType;

    public ProductItemAdapter(Context context, List<ProductItemObject> productItemObjectList, Spinner spinner){
        this.productList = productItemObjectList;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.docType = spinner;
    }

    public ProductItemAdapter(Context context, JSONArray jsonArray, Spinner spinner) throws JSONException {
        productList = new ArrayList<>();
        this.docType = spinner;
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

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        convertView = this.mInflater.inflate(R.layout.stock_product_item, null);

        final CheckBox isChosenCheckBox = convertView.findViewById(R.id.stockDocProductCheckBox);
        TextView stockDocProductNameTextView = convertView.findViewById(R.id.stockDocProductNameTextView);
        final EditText stockDocQtyTextView = convertView.findViewById(R.id.stockDocQtyTextView);
        TextView stockDocIdTextView = convertView.findViewById(R.id.stockDocIdTextView);
        TextView stockItemQtyTextView = convertView.findViewById(R.id.stockItemQtyTextView);
        ImageButton addBtn = convertView.findViewById(R.id.addBtn);
        ImageButton minusBtn = convertView.findViewById(R.id.minusBtn);

        final ProductItemObject object = productList.get(position);

        isChosenCheckBox.setChecked(object.isChosen());
        stockDocProductNameTextView.setText(object.getProduct().getName());
        stockDocQtyTextView.setText((object.getQty()+""));
        stockDocIdTextView.setText((object.getProduct().getId()+""));
        stockItemQtyTextView.setText(
                ("В наличии: "
                        +": "
                        +(object.getProduct().getInStockQty())));

        stockDocQtyTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                double qty = (s.toString().isEmpty()) ? 0.0 : Double.parseDouble(s.toString());
                if (qty < 0.0){
                    stockDocQtyTextView.setText((0.0+""));
                    return;
                }
                if (qty == 0.0){
                    isChosenCheckBox.setChecked(false);
                    object.setChosen(false);
                    object.setQty(0.0);
                    return;
                }
                if ((docType.getSelectedItemId() == 1)){ // check if out
                    if (qty > object.getProduct().getInStockQty()){
                        qty = object.getProduct().getInStockQty();
                        stockDocQtyTextView.setText((qty+""));
                        return;
                        //qty = object.getProduct().getInStockQty();
                    }
                }
                isChosenCheckBox.setChecked(true);
                object.setChosen(true);
                object.setQty(qty);
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

        return convertView;
    }
}

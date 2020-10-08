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
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.kamilamalikova.help.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class ProductItemAdapter extends BaseAdapter {
    List<Boolean> isChosenList;
    List<String> productNameList;
    List<Double> stockQtyList;
    List<String> stockDocIdList;
    LayoutInflater mInflater;

    public ProductItemAdapter(Context context, List<Boolean> isChosenList, List<String> productNameList, List<Double> stockQtyList, List<String> stockDocIdList) {
        this.isChosenList = isChosenList;
        this.productNameList = productNameList;
        this.stockQtyList = stockQtyList;
        this.stockDocIdList = stockDocIdList;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ProductItemAdapter(Context context, List<ProductItemObject> productItemObjectList){
        isChosenList = new ArrayList<>();
        productNameList = new ArrayList<>();
        stockQtyList = new ArrayList<>();
        stockDocIdList = new ArrayList<>();

        for (ProductItemObject object: productItemObjectList) {
            isChosenList.add(object.isChosen);
            productNameList.add(object.getProductName());
            stockQtyList.add(object.getQty());
            stockDocIdList.add(object.getId());
        }
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ProductItemAdapter(Context context, JSONArray jsonArray) throws JSONException {
        isChosenList = new ArrayList<>();
        productNameList = new ArrayList<>();
        stockQtyList = new ArrayList<>();
        stockDocIdList = new ArrayList<>();
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            isChosenList.add(false);
            productNameList.add(object.getString("name"));
            stockQtyList.add(0.0);
            stockDocIdList.add(object.getString("id"));
        }
    }

    @Override
    public int getCount() {
        return productNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return new ProductItemObject(isChosenList.get(position), stockDocIdList.get(position), productNameList.get(position), stockQtyList.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") final View view = this.mInflater.inflate(R.layout.stock_product_item, null);
        final CheckBox isChosenCheckBox = view.findViewById(R.id.stockDocProductCheckBox);
        TextView stockDocProductNameTextView = view.findViewById(R.id.stockDocProductNameTextView);
        final EditText stockDocQtyTextView = view.findViewById(R.id.stockDocQtyTextView);
        TextView stockDocIdTextView = view.findViewById(R.id.stockDocIdTextView);

        isChosenCheckBox.setChecked(isChosenList.get(position));
        stockDocProductNameTextView.setText(productNameList.get(position));
        stockDocQtyTextView.setText(Double.toString(stockQtyList.get(position)));
        stockDocIdTextView.setText(stockDocIdList.get(position));

        stockDocQtyTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == KeyEvent.KEYCODE_CALL){
                    InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(stockDocQtyTextView.getWindowToken(), 0);
                    if (Double.parseDouble(stockDocQtyTextView.getText().toString()) > 0.0) {
                        isChosenCheckBox.setChecked(true);
                        isChosenList.set(position, true);
                        stockQtyList.set(position, Double.parseDouble(stockDocQtyTextView.getText().toString()));
                    }
                    stockDocQtyTextView.setText(
                    Double.parseDouble(stockDocQtyTextView.getText().toString())+""
                    );
                    return true;
                }
                return false;
            }
        });
        stockDocQtyTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!stockDocQtyTextView.getText().toString().isEmpty()){
                    if ( Double.parseDouble(stockDocQtyTextView.getText().toString()) > 0.0) {
                        isChosenList.set(position, true);
                        stockQtyList.set(position, Double.parseDouble(stockDocQtyTextView.getText().toString()));
                        isChosenCheckBox.setChecked(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }
}

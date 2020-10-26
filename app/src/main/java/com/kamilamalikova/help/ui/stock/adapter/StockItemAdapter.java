package com.kamilamalikova.help.ui.stock.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.navigation.Navigation;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.ui.products.adapter.ItemObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StockItemAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    String[] items;
    String[] index;
    String[] qty;
    String[] unit;
    @LayoutRes
    int layoutRes;

    public StockItemAdapter(Context c, String[] index, String[] items, String[] qty, String[] unit){
        this.index = index;
        this.items = items;
        this.qty = qty;
        this.unit = unit;
        this.mInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public StockItemAdapter(Context c, JSONArray jsonArrayResponse, @LayoutRes int layoutRes) throws JSONException {
        index = new String[jsonArrayResponse.length()];
        items = new String[jsonArrayResponse.length()];
        qty = new String[jsonArrayResponse.length()];
        unit = new String[jsonArrayResponse.length()];
        for (int i = 0; i < jsonArrayResponse.length(); i++) {
            JSONObject object = (JSONObject) jsonArrayResponse.get(i);
            index[i] = Integer.toString((int)object.get("id"));
            items[i] = (String) object.get("productName");
            qty[i] = Double.toString((Double) object.get("inStockQty"));
            JSONObject unitObject = (JSONObject) object.get("unit");
            unit[i] = unitObject.getString("unitName");
        }
        this.mInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutRes = layoutRes;
    }

    public StockItemAdapter(Context c, JSONArray jsonArrayResponse, String productName, @LayoutRes int layoutRes) throws JSONException {
        index = new String[jsonArrayResponse.length()];
        items = new String[jsonArrayResponse.length()];
        qty = new String[jsonArrayResponse.length()];
        unit = new String[jsonArrayResponse.length()];
        for (int i = 0; i < jsonArrayResponse.length(); i++) {
            JSONObject object = (JSONObject) jsonArrayResponse.get(i);
            index[i] = Integer.toString((int)object.get("id"));
            items[i] = (String) object.get(productName);
            qty[i] = Double.toString((Double) object.get("inStockQty"));
            JSONObject unitObject = (JSONObject) object.get("unit");
            unit[i] = unitObject.getString("unitName");
        }
        this.mInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutRes = layoutRes;
    }


    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return new ItemObject(index[position],items[position]);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(this.layoutRes, null);
        }
        TextView indexTextView = convertView.findViewById(R.id.productIdItemTextView);
        TextView itemNameTextView = convertView.findViewById(R.id.productNameItemTextView);
        TextView itemQtyTextView = convertView.findViewById(R.id.productQtyItemTextView);
        //itemQtyTextView.setVisibility(View.INVISIBLE);
        TextView itemUnitTextView = convertView.findViewById(R.id.qtyName);
        String item = this.items[position];
        final String index = this.index[position];
        String unit = this.unit[position];
        String qty = this.qty[position];
        indexTextView.setText(index);
        itemNameTextView.setText(item);
        itemUnitTextView.setText(unit);
        itemQtyTextView.setText(qty);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("productId", index);
                Navigation.findNavController(v).navigate(R.id.nav_stock_item, bundle);
            }
        });
        return convertView;
    }

}


package com.kamilamalikova.help.ui.products.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.SettingsObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ItemAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    String[] items;
    String[] index;
    @LayoutRes
    int layoutRes;

    public ItemAdapter(Context c, String[] index, String[] items){
        this.index = index;
        this.items = items;
        this.mInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutRes = layoutRes;
    }

    public ItemAdapter(Context c,JSONArray jsonArrayResponse, String itemName, @LayoutRes int layoutRes) throws JSONException {
        index = new String[jsonArrayResponse.length()+1];
        items = new String[jsonArrayResponse.length()+1];
        int i = 0;
        for ( ; i < jsonArrayResponse.length(); i++) {
            JSONObject object = (JSONObject) jsonArrayResponse.get(i);
            index[i] = Integer.toString((int)object.get("id"));
            items[i] = (String) object.get(itemName);
        }
        index[i] = "500";
        items[i] = "";
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
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View v = mInflater.inflate(this.layoutRes, null);
        TextView indexTextView = v.findViewById(R.id.spinItemIndexTextView);
        TextView itemNameTextView = v.findViewById(R.id.spinItemNameTextView);
        String item = this.items[position];
        String index = this.index[position];
        indexTextView.setText(index);
        itemNameTextView.setText(item);
        return v;
    }

}


package com.kamilamalikova.help.ui.settings.adapter;

import android.content.Context;
import android.content.Intent;
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
    }

    public ItemAdapter(Context c,JSONArray jsonArrayResponse, String itemName) throws JSONException {
        index = new String[jsonArrayResponse.length()];
        items = new String[jsonArrayResponse.length()];
        for (int i = 0; i < jsonArrayResponse.length(); i++) {
            JSONObject object = (JSONObject) jsonArrayResponse.get(i);
            index[i] = Integer.toString((int)object.get("id"));
            items[i] = (String) object.get(itemName);
        }
        this.mInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return new SettingsObject(index[position],items[position]);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.list_detail, null);
        TextView indexTextView = v.findViewById(R.id.itemIndexTextView);
        TextView itemNameTextView = v.findViewById(R.id.itemNameTextView);

        String item = this.items[position];
        String index = this.index[position];

        indexTextView.setText(index);
        itemNameTextView.setText(item);
        return v;
    }

}

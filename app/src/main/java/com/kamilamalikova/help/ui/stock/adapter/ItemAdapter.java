package com.kamilamalikova.help.ui.stock.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.RequiresApi;

import com.kamilamalikova.help.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ItemAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    String[] type;
    String[] id;
    LocalDateTime date[];
    int layoutRes;

    public ItemAdapter(Context context, String[] type, String[] id, LocalDateTime date[], int layoutRes) {
        this.type = type;
        this.id = id;
        this.date = date;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutRes = layoutRes;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return new ItemObject(id[position], type[position], date[position]);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") View v = mInflater.inflate(this.layoutRes, null);
        TextView idTextView = v.findViewById(R.id.itemDocIdTextView);
        TextView typeTextView = v.findViewById(R.id.itemDocTypeTextView);
        TextView dateTextView = v.findViewById(R.id.itemDocDateTextView);

        String id = this.id[position];
        String type = this.type[position];
        LocalDateTime date = this.date[position];

        idTextView.setText(id);
        typeTextView.setText(type);
        dateTextView.setText(date.format(DateTimeFormatter.ofPattern("dd.mm.yyyy")));
        return v;
    }
}

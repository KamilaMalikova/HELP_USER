package com.kamilamalikova.help.ui.users.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.Role;
import com.kamilamalikova.help.ui.users.UsersFragment;

import java.util.ArrayList;
import java.util.List;

public class RoleAdapter extends BaseAdapter {
    List<Role> roles;
    LayoutInflater mInflater;

    public RoleAdapter(Context context) {
        this.roles = new ArrayList<>();
        roles.add(Role.WAITER);
        roles.add(Role.STUFF);
        roles.add(Role.OWNER);
        roles.add(Role.NOTWORKING);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getItemPosition(Role role){
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).name().equals(role.name())) return i;
        }
        return -1;
    }

    @Override
    public int getCount() {
        return roles.size();
    }

    @Override
    public Object getItem(int position) {
        return roles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.spin_item, null);
        }
        TextView spinItemNameTextView = convertView.findViewById(R.id.spinItemNameTextView);
        spinItemNameTextView.setText(roles.get(position).getRu_name());
        return convertView;
    }

    public void add(int position, Role role) {
        roles.add(position, role);
        notifyDataSetChanged();
    }


}

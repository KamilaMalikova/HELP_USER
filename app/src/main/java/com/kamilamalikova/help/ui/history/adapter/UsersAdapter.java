package com.kamilamalikova.help.ui.history.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends ArrayAdapter<User> {
    List<User> users, tempItems, suggestions;
    LayoutInflater inflater;

    public UsersAdapter(Context context, ArrayList<User> users){
        super(context, R.layout.user_item, users);
        this.users = users;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tempItems = new ArrayList<>(users);
        this.suggestions = new ArrayList<>();
    }

    public List<User> getUsers() {
        return users;
    }


    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) convertView = inflater.inflate(R.layout.user_item, null);
        TextView nameTextView = convertView.findViewById(R.id.userItemNameTextView);
        TextView idTextView = convertView.findViewById(R.id.userItemIndexTextView);
        idTextView.setVisibility(View.INVISIBLE);

        nameTextView.setText((users.get(position).getName()+" "+users.get(position).getLastname()));
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            User user = (User) resultValue;
            return user.getName()+" "+user.getLastname();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (User user : tempItems) {
                    if (user.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(user);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            ArrayList<User> tempValues = (ArrayList<User>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (User userObj : tempValues) {
                    add(userObj);
                }
                notifyDataSetChanged();
            } else {
                clear();
                notifyDataSetChanged();
            }
        }
    };
}

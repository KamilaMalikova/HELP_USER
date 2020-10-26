package com.kamilamalikova.help.ui.users.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.Keyboard;
import com.kamilamalikova.help.model.Product;
import com.kamilamalikova.help.model.Role;
import com.kamilamalikova.help.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<User> users;
    List<User> usersOrigin;
    Context context;
    View view;

    private static final int LOADING = 0;
    private static final int ITEM = 1;
    private boolean isLoadingAdded = false;

    public UsersAdapter(Context context, JSONArray paramsArray, View view) throws JSONException {
        this.users = new ArrayList<>();
        this.usersOrigin = new ArrayList<>();
        this.context = context;
        this.view = view;
        for (int i = 0; i < paramsArray.length(); i++) {
            JSONObject object = paramsArray.getJSONObject(i);
            this.users.add(new User(object));
            this.usersOrigin.add(new User(object));
        }
    }


    public UsersAdapter(Context context, View view){
        this.context = context;
        this.users = new ArrayList<>();
        this.usersOrigin = new ArrayList<>();
        this.view = view;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case ITEM:
                View viewItem = inflater.inflate(R.layout.user_item, parent, false);
                viewHolder = new ViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = users.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                UsersAdapter.ViewHolder viewHolder = (UsersAdapter.ViewHolder) holder;
                viewHolder.nameTextView.setText((user.getName()+" "+user.getLastname()));
                viewHolder.idTextView.setText((user.getId()+""));
                viewHolder.user = user;
                break;
            case LOADING:
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount () {
        return users == null ? 0 : users.size();
    }


    @Override
    public int getItemViewType(int position) {
        return (position == users.size()-1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void addLoadingFooter(String username, String password, String name, String lastname, Role role){
        isLoadingAdded = true;
        //add(username, password, name, lastname, role);
    }

    public void addLoadingFooter(){
        isLoadingAdded = true;
//        add(null, null, null, null, Role.NOTWORKING);
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
    }

    public void add (List<User> users){
//        if (usersOrigin.size() > 0){
//            usersOrigin.remove(usersOrigin.size()-1);
//            users.remove(users.size()-1);
//        }
        for (User user: users) {
            this.users.add(user);
            this.usersOrigin.add(user);
        }
        notifyDataSetChanged();
    }

    public void add (String username, String password, String name, String lastname, Role role){
        this.users.add(new User(username, password, name, lastname, role));
        this.usersOrigin.add(new User(username, password, name, lastname, role));
        notifyDataSetChanged();
    }

    public User getItem(int position){
        return users.get(position);
    }

    public void add(JSONArray paramsArray) throws JSONException {
//        if (usersOrigin.size() > 0){
//            usersOrigin.remove(usersOrigin.size()-1);
//            users.remove(users.size()-1);
//        }
        for (int i = 0; i < paramsArray.length(); i++) {
            JSONObject object = paramsArray.getJSONObject(i);
            this.users.add(new User(object));
            this.usersOrigin.add(new User(object));
        }
        notifyDataSetChanged();
    }

    public void setUsersList(ArrayList<User> users) {
        this.users.clear();
        this.users.addAll(users);
        this.usersOrigin.clear();
        this.usersOrigin.addAll(users);
    }

    public void init() {
        this.users = new ArrayList<>();
        this.usersOrigin = new ArrayList<>();
        notifyDataSetChanged();
/*        this.usersOrigin.clear();
        this.users.clear();*/
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.loadmore_progress);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView;
        TextView idTextView;
        User user;
        AlertDialog.Builder builder;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.userItemNameTextView);
            idTextView = itemView.findViewById(R.id.userItemIndexTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", user);
                    Keyboard.hideKeyboard(context);
                    Navigation.findNavController(view).navigate(R.id.nav_user, bundle);
                }
            });
        }
    }

    public void filter(String query){
        query = query.toLowerCase();
        users.clear();
        if (query.isEmpty()){
            users.addAll(usersOrigin);
        }else {
            for (User user: usersOrigin) {
                if (user.getName().toLowerCase().contains(query) || user.getLastname().contains(query) || user.getUsername().toLowerCase().contains(query) || user.getRole().getRu_name().toLowerCase().contains(query)){
                    users.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void clear(){
        users.clear();
        usersOrigin.clear();
    }
}

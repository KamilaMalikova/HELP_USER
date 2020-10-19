package com.kamilamalikova.help.ui.terminal.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.EatingPlace;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.Order;
import com.kamilamalikova.help.model.OrderStatus;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class TableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;

    List<EatingPlace> eatingPlaceList;
    LoggedInUser user;

    Context context;

    private static final int LOADING = 0;
    private static final int ITEM = 1;
    private boolean isLoadingAdded = false;


    public TableAdapter(Context context, JSONArray paramsArray) throws JSONException {

        this.eatingPlaceList = new ArrayList<>();

        this.context = context;
        for (int i = 0; i < paramsArray.length(); i++) {
            JSONObject object = paramsArray.getJSONObject(i);

            this.eatingPlaceList.add(new EatingPlace(object.getInt("id"),
                    object.getBoolean("reserved"),
                    object.getString("waiterUsername"),
                    object.getString("waiterName"),
                    object.getBoolean("active")));
        }
    }

    public TableAdapter(Context context, Activity activity){
        this.eatingPlaceList = new ArrayList<>();
        this.context = context;
        this.activity = activity;
        user = LoggedInUser.isLoggedIn(context, activity);
    }

    public List<EatingPlace> getEatingPlaceList() {
        return eatingPlaceList;
    }

    public void setEatingPlaceList(List<EatingPlace> eatingPlaceList) {
        this.eatingPlaceList = eatingPlaceList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case ITEM:
                View viewItem = inflater.inflate(R.layout.table_item_layout, parent, false);
                viewHolder = new ViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingViewHolder(viewLoading);
                break;
        }

        return viewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        EatingPlace eatingPlace = eatingPlaceList.get(position);
        switch (getItemViewType(position)){
            case ITEM:
                ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.username.setText(eatingPlace.getWaiterUsername());
                if (eatingPlace.getWaiterUsername().equals("null")) {
                    viewHolder.name.setText("Свободно");
                    viewHolder.cardView.setBackgroundResource(R.color.main_color);
                }
                else {
                    viewHolder.name.setText(eatingPlace.getWaiterName());
                    viewHolder.cardView.setBackgroundResource(R.color.orange);
                }
                viewHolder.eatingPlace = eatingPlace;
                viewHolder.table.setText((eatingPlace.getId()+""));
                break;
            case LOADING:
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.tablesProgressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return eatingPlaceList == null ? 0 : eatingPlaceList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == eatingPlaceList.size()-1 && isLoadingAdded) ? LOADING : ITEM;
//                super.getItemViewType(position);
    }

    public void addLoadingFooter(String waiterUsername, int tableId, String waiterName, boolean reserved, boolean active){
        isLoadingAdded = true;
        add(waiterUsername, tableId, waiterName, reserved, active);
    }

    public void addLoadingFooter(){
        isLoadingAdded = true;
        //add("null", 0, "free", false, false);
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
    }

    public void add (List<EatingPlace> eatingPlaces){
        eatingPlaceList.addAll(eatingPlaces);
    }

    public void add (String waiterUsername, int tableId, String waiterName, boolean reserved, boolean active){
        this.eatingPlaceList.add(new EatingPlace(tableId, reserved, waiterUsername, waiterName, active));
        notifyItemInserted(this.eatingPlaceList.size()-1);
    }

    public EatingPlace getItem(int position){
        return eatingPlaceList.get(position);
    }

    public void add(JSONArray paramsArray) throws JSONException {
        for (int i = 0; i < paramsArray.length(); i++) {
            JSONObject object = paramsArray.getJSONObject(i);
            this.eatingPlaceList.add(new EatingPlace(object.getInt("id"),
                    object.getBoolean("reserved"),
                    object.getString("waiterUsername"),
                    object.getString("waiterName"),
                    object.getBoolean("active")));
        }
        notifyItemInserted(this.eatingPlaceList.size()-1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView username;
        TextView table;
        TextView name;
        CardView cardView;
        EatingPlace eatingPlace;
        AlertDialog.Builder builder;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.waiterUserNameTextView);
            table = itemView.findViewById(R.id.tableIdTextView);
            name = itemView.findViewById(R.id.waiterNameTextView);
            cardView = itemView.findViewById(R.id.cardView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!eatingPlace.isReserved()){
                        builder = new AlertDialog.Builder(itemView.getContext());

                        builder.setMessage(R.string.start_new_order)
                                .setCancelable(true)
                                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        reserveTable(URLs.POST_TABLE.getName(), eatingPlace, itemView);
                                    }
                                })
                                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();

                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    else {
                        requestOrder(URLs.GET_ORDERS.getName()+"/0", eatingPlace, itemView);
                    }
                }
            });
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar tablesProgressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            tablesProgressBar = itemView.findViewById(R.id.loadmore_progress);
        }
    }


    private void reserveTable(String url, EatingPlace eatingPlace, final View view){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url+"/"+eatingPlace.getId());

        LoggedInUser loggedInUser = user;
        assert loggedInUser != null;
        eatingPlace.setWaiterUsername(loggedInUser.getUsername());
        requestPackage.setParam("reserved", "1");
        requestPackage.setParam("username", loggedInUser.getUsername());

        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(requestPackage.getJsonObject().toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        Log.i("SER", requestPackage.getFullUrl() + entity);
        Log.i("SER", requestPackage.getFullUrl() + requestPackage.getJsonObject());

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(context.getString(R.string.authorizationToken), loggedInUser.getAuthorizationToken());
        client.post(context, requestPackage.getFullUrl(), entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody));
                    Log.i("response", response.toString());
                    navigate(view,  new EatingPlace(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Status", statusCode+"! "+new String(responseBody));
            }
        });

    }


    private void requestOrder(String url, final EatingPlace eatingPlace, final View view){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.GET);
        requestPackage.setUrl(url);
        requestPackage.setParam("tableId", eatingPlace.getId()+"");
        requestPackage.setParam("status", OrderStatus.CREATED.name());
        LoggedInUser loggedInUser = user;
        assert loggedInUser != null;

        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(requestPackage.getJsonObject().toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        Log.i("SER", requestPackage.getFullUrl() + entity);
        Log.i("SER", requestPackage.getFullUrl() + requestPackage.getJsonObject());

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(context.getString(R.string.authorizationToken), loggedInUser.getAuthorizationToken());
        client.get(context, requestPackage.getFullUrl(), entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject responseObject = new JSONObject(new String(responseBody));
                    Log.i("response", responseObject.toString());
                    JSONArray responseArray = responseObject.getJSONArray("content");

                    if (responseArray.length() == 0){
                        navigate(view, eatingPlace);
                    }
                    else {
                        Order order = new Order(responseArray.getJSONObject(0));
                        navigate(view, eatingPlace, order);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Status", statusCode+"! "+new String(responseBody));
            }
        });

    }

    private void navigate(View itemView, EatingPlace eatingPlace){
        Bundle bundle = new Bundle();
        bundle.putParcelable("table", eatingPlace);
        Navigation.findNavController(itemView).navigate(R.id.nav_menu, bundle);
    }

    private void navigate(View itemView, EatingPlace eatingPlace, Order order){
        Bundle bundle = new Bundle();
        bundle.putParcelable("table", eatingPlace);
        bundle.putParcelable("order", order);
        Navigation.findNavController(itemView).navigate(R.id.nav_order, bundle);
    }
}

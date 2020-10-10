package com.kamilamalikova.help.ui.terminal.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.EatingPlace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<EatingPlace> eatingPlaceList;

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

    public TableAdapter(Context context){
        this.eatingPlaceList = new ArrayList<>();
        this.context = context;
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
                if (eatingPlace.getWaiterUsername().equals("free")) {
                    viewHolder.name.setText("Свободно");
                    viewHolder.cardView.setBackgroundResource(R.color.free);
                }
                else {
                    viewHolder.name.setText(eatingPlace.getWaiterName());
                }
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
//        int position = eatingPlaceList.size() - 1;
//        EatingPlace result = getItem(position);
//
//        if (result != null) {
//            eatingPlaceList.remove(position);
//            notifyItemRemoved(position);
//        }
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
                    builder = new AlertDialog.Builder(itemView.getContext());

                    builder.setMessage(R.string.start_new_order);

                    builder.setMessage(R.string.start_new_order)
                            .setCancelable(false)
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(itemView.getContext(),"New dialog", Toast.LENGTH_LONG)
                                            .show();
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

}

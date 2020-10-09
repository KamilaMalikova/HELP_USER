package com.kamilamalikova.help.ui.terminal.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kamilamalikova.help.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {
    List<String> waiterUsernameList;
    List<Integer> tableList;
    List<String> waiterNameList;
    List<Boolean> reservedList;
    LayoutInflater mInflater;

    public TableAdapter(Context context, List<String> waiterUsernameList, List<Integer> tableList, List<String> waiterNameList, List<Boolean> reserved) {
        this.waiterUsernameList = waiterUsernameList;
        this.tableList = tableList;
        this.waiterNameList = waiterNameList;
        this.reservedList = reserved;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public TableAdapter(Context context, JSONArray paramsArray) throws JSONException {
        waiterNameList = new ArrayList<>();
        tableList = new ArrayList<>();
        waiterUsernameList = new ArrayList<>();
        reservedList = new ArrayList<>();
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < paramsArray.length(); i++) {
            JSONObject object = paramsArray.getJSONObject(i);
            tableList.add(object.getInt("id"));
            waiterUsernameList.add(object.getString("waiterUsername"));
            waiterNameList.add(object.getString("waiterName"));
            reservedList.add(object.getBoolean("reserved"));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.table_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.username.setText(waiterUsernameList.get(position));
        if (waiterNameList.get(position).equals("free")) {
            holder.name.setText("Свободно");
            holder.cardView.setBackgroundResource(R.color.free);
        }
        else {
            holder.name.setText(waiterNameList.get(position));
        }
        holder.table.setText((tableList.get(position)+""));
    }

    @Override
    public int getItemCount() {
        return tableList.size();
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

}

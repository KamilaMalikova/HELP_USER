package com.kamilamalikova.help.ui.stock.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.DOCTYPE;
import com.kamilamalikova.help.model.StockDocument;
import com.kamilamalikova.help.model.StockInventory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StockDocAdapter extends BaseExpandableListAdapter {
    Context context;
    List<Integer> stockDocIdList;
    List<String> stockDocTypeList;
    List<LocalDateTime> stockDocDateTimeList;
    HashMap<Integer,List<StockInventory>> inventoriesList;

    LayoutInflater mInflater;

    public StockDocAdapter(Context context, List<Integer> stockDocIdList,
                           List<String> stockDocTypeList,
                           List<LocalDateTime> stockDocDateTimeList,
                           HashMap<Integer,List<StockInventory>> inventoriesList) {
        this.stockDocIdList = stockDocIdList;
        this.stockDocTypeList = stockDocTypeList;
        this.stockDocDateTimeList = stockDocDateTimeList;
        this.inventoriesList = inventoriesList;
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public StockDocAdapter(Context context, JSONArray responseArray) throws JSONException {
        this.stockDocIdList = new ArrayList<>();
        this.stockDocTypeList = new ArrayList<>();
        this.stockDocDateTimeList = new ArrayList<>();
        this.inventoriesList = new HashMap<>();

        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject jsonObject = responseArray.getJSONObject(i);
            stockDocIdList.add(jsonObject.getInt("documentId"));
            stockDocTypeList.add(jsonObject.getString("documentType"));
            stockDocDateTimeList.add(LocalDateTime.parse(jsonObject.getString("date")));

            JSONArray inventoriesJsonArray = jsonObject.getJSONArray("inventories");
            List<StockInventory> inventories = new ArrayList<>();
            for (int j = 0; j < inventoriesJsonArray.length(); j++) {
                JSONObject inventory = inventoriesJsonArray.getJSONObject(j);
                inventories.add(new StockInventory(inventory));
            }
            inventoriesList.put(jsonObject.getInt("documentId"), inventories);
        }
        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public void init(){
        this.stockDocIdList = new ArrayList<>();
        this.stockDocTypeList = new ArrayList<>();
        this.stockDocDateTimeList = new ArrayList<>();
        this.inventoriesList = new HashMap<>();

    }

    public StockDocAdapter(Context context){
        this.stockDocIdList = new ArrayList<>();
        this.stockDocTypeList = new ArrayList<>();
        this.stockDocDateTimeList = new ArrayList<>();
        this.inventoriesList = new HashMap<>();

        this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public void add(JSONArray responseArray) throws JSONException {
        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject jsonObject = responseArray.getJSONObject(i);
            stockDocIdList.add(jsonObject.getInt("documentId"));
            stockDocTypeList.add(jsonObject.getString("documentType"));
            stockDocDateTimeList.add(LocalDateTime.parse(jsonObject.getString("date")));

            JSONArray inventoriesJsonArray = jsonObject.getJSONArray("inventories");
            List<StockInventory> inventories = new ArrayList<>();
            for (int j = 0; j < inventoriesJsonArray.length(); j++) {
                JSONObject inventory = inventoriesJsonArray.getJSONObject(j);
                inventories.add(new StockInventory(inventory));
            }
            inventoriesList.put(jsonObject.getInt("documentId"), inventories);
        }
    }

    @Override
    public int getGroupCount() {
        return stockDocIdList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.inventoriesList.get(this.stockDocIdList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return new StockDocument(stockDocIdList.get(groupPosition),
                stockDocTypeList.get(groupPosition),
                stockDocDateTimeList.get(groupPosition),
                inventoriesList.get(groupPosition));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.inventoriesList.get(groupPosition)
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = mInflater.inflate(R.layout.stock_document_item, null);
        }

        TextView idTextView = convertView.findViewById(R.id.itemDocIdTextView);
        TextView typeTextView = convertView.findViewById(R.id.itemDocTypeTextView);
        TextView dateTextView = convertView.findViewById(R.id.itemDocDateTextView);


        idTextView.setText((Integer.toString(stockDocIdList.get(groupPosition))));
        typeTextView.setText((stockDocTypeList.get(groupPosition).equals(DOCTYPE.IN.getName())) ? DOCTYPE.IN.getName_ru() : DOCTYPE.OUT.getName_ru());
        LocalDateTime date = stockDocDateTimeList.get(groupPosition);

        dateTextView.setText((date.getDayOfMonth()+"."+date.getMonthValue()+"."+date.getYear()));


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.inventory_item, null);
        }

        TextView idTextView = convertView.findViewById(R.id.stockDocInventoryIdTextView);
        TextView nameTextView = convertView.findViewById(R.id.stockDocInventoryNameTextView);
        TextView qtyTextView = convertView.findViewById(R.id.stockDocInventoryQtyTextView);

        StockInventory inventory = inventoriesList.get(stockDocIdList.get(groupPosition)).get(childPosition);

        idTextView.setText((inventory.getProductId()+""));
        nameTextView.setText(inventory.getProductName());
        qtyTextView.setText((Double.toString(inventory.getAmount())));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

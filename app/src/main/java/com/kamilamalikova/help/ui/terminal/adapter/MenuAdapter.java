package com.kamilamalikova.help.ui.terminal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.Product;
import com.kamilamalikova.help.ui.terminal.fragments.MenuFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MenuAdapter extends BaseExpandableListAdapter {
    Context context;
    LayoutInflater mInflater;
    List<String> categories;
    List<String> categoriesOriginal;
    MenuFragment menuFragment;
    HashMap<String, List<Product>> productListHashMap;
    HashMap<String, List<Product>> productListHashMapOriginal;

    public MenuAdapter(Context context, JSONObject products, MenuFragment menuFragment) throws JSONException {
        this.context = context;
        this.menuFragment = menuFragment;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.productListHashMap = new HashMap<>();
        this.categories = new ArrayList<>();
        this.productListHashMapOriginal = new HashMap<>();
        this.categoriesOriginal = new ArrayList<>();

        for (Iterator<String> it = products.keys(); it.hasNext(); ) {
            String key = it.next();
            categories.add(key);
            categoriesOriginal.add(key);
            List<Product> products1 = new ArrayList<>();
            List<Product> products2 = new ArrayList<>();
            JSONArray array = products.getJSONArray(key);
            for (int j = 0; j < array.length(); j++) {
                products1.add(new Product(array.getJSONObject(j)));
                products2.add(new Product(array.getJSONObject(j)));
            }
            this.productListHashMap.put(key, products1);
            this.productListHashMapOriginal.put(key, products2);
        }
        
    }

    @Override
    public int getGroupCount() {
        return categories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return productListHashMap.get(categories.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return productListHashMap.get(categories.get(groupPosition)).get(childPosition);
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
            convertView = mInflater.inflate(R.layout.category_item_layout, null);
        }
        TextView itemCategoryTextView = convertView.findViewById(R.id.itemCategoryTextView);
        itemCategoryTextView.setText(categories.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.menu_product_item_layout, null);
        }

        TextView menuItemId = convertView.findViewById(R.id.menuItemId);
        ImageButton addFromMenuBtn = convertView.findViewById(R.id.addFromMenuBtn);
        TextView menuItemNameTextView = convertView.findViewById(R.id.menuItemNameTextView);
        final TextView menuItemQtyTextView = convertView.findViewById(R.id.menuItemQtyTextView);
        TextView menuItemCostTextView = convertView.findViewById(R.id.menuItemCostTextView);
        final EditText menuItemSelectedQtyEditText = convertView.findViewById(R.id.menuItemSelectedQtyTextView);
        menuItemSelectedQtyEditText.setText("0.0");
        final Product product = productListHashMap.get(categories.get(groupPosition)).get(childPosition);

        ImageButton minusFromMenuBtn = convertView.findViewById(R.id.minusFromMenuBtn);

        menuItemId.setText((product.getId()+""));
        menuItemNameTextView.setText((product.getProductName()+""));
        menuItemQtyTextView.setText(("Кол-во: "+" "+(product.getInStockQty() - product.getBuyQty())+""));
        menuItemCostTextView.setText(("Цена: "+": "+product.getCost()));

        final View finalConvertView = convertView;

        addFromMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double qty = (menuItemSelectedQtyEditText.getText().toString().isEmpty()) ? 0.0 : Double.parseDouble(menuItemSelectedQtyEditText.getText().toString());
                qty+=1.0;
                if (qty > product.getInStockQty()){
                    Toast.makeText(finalConvertView.getContext(), "Превышен лимит", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                product.setBuyQty(qty);
                menuItemSelectedQtyEditText.setText((product.getBuyQty()+""));
                menuItemQtyTextView.setText(("Кол-во: "+" "+ (product.getInStockQty() - product.getBuyQty()) +""));
                menuFragment.orderedProducts.add(product);
                menuFragment.orderBtn.setText((context.getText(R.string.order)+" ("+menuFragment.orderedProducts.size()+")"));
//                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
//                popupWindow.setTouchable(true);
//                popupWindow.setOutsideTouchable(true);
//                //popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                popupWindow.setFocusable(true);
            }
        });


        minusFromMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double qty = (menuItemSelectedQtyEditText.getText().toString().isEmpty()) ? 0.0 : Double.parseDouble(menuItemSelectedQtyEditText.getText().toString());
                qty-=1.0;
                if (qty < 0.0) {
                  return;
                }
                product.setBuyQty(qty);
                menuItemSelectedQtyEditText.setText((product.getBuyQty()+""));
                menuItemQtyTextView.setText(("Кол-во: "+" "+ (product.getInStockQty() - product.getBuyQty()) +""));
                if (qty == 0.0){
                    menuFragment.orderBtn.setText((context.getText(R.string.order)+" ("+menuFragment.orderedProducts.size()+")"));
                }else {
                    menuFragment.orderedProducts.add(product);
                    menuFragment.orderBtn.setText((context.getText(R.string.order)+" ("+menuFragment.orderedProducts.size()+")"));
                }

            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void filter(String query){
        query = query.toLowerCase();
        categories.clear();
        productListHashMap.clear();
        if (query.isEmpty()){
            categories.addAll(categoriesOriginal);
            productListHashMap.putAll(productListHashMapOriginal);
        }else {
            for (String category: categoriesOriginal) {
                List<Product> productList = productListHashMapOriginal.get(category);
                List<Product> newProductList = new ArrayList<>();
                for (Product product: productList) {
                    if (product.getProductName().toLowerCase().contains(query) || product.getCategory().getCategory().toLowerCase().contains(query)){
                        newProductList.add(product);
                    }
                }
                if (newProductList.size() > 0){
                    categories.add(category);
                    productListHashMap.put(category, newProductList);
                }
            }
        }
        notifyDataSetChanged();
    }
}

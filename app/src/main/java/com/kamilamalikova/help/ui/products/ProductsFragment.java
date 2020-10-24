package com.kamilamalikova.help.ui.products;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamilamalikova.help.LogInActivity;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.ui.products.adapter.ItemAdapter;
import com.kamilamalikova.help.ui.products.adapter.ItemObject;
import com.kamilamalikova.help.ui.products.adapter.ProductItemAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;


public class ProductsFragment extends Fragment {
    SessionManager sessionManager;

    LayoutInflater layoutInflater;
    View view;
    View popupView;
    PopupWindow popupWindow;
    ListView productsListView;
    SwipeRefreshLayout swipeView;
    String productName;
    String category;
    boolean active;
    boolean restaurant;
    Button filterBtn;
    Spinner categorySpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        layoutInflater = inflater;
        view = inflater.inflate(R.layout.fragment_products, container, false);
        productsListView = view.findViewById(R.id.productsListView);
        swipeView = view.findViewById(R.id.productsListScroll);
        sessionManager = new SessionManager(view.getContext());

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    requestData(URLs.GET_PRODUCTS.getName(), null, "0", true, true);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                swipeView.setRefreshing(false);
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fabAddProduct);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                try {
                    Navigation.findNavController(view).navigate(R.id.action_product_to_add_product);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String productId = ((ItemObject)productsListView.getAdapter().getItem(position)).getId();
                Bundle bundle = new Bundle();
                bundle.putString("productId", productId);
                Navigation.findNavController(view).navigate(R.id.nav_product_detail, bundle);
            }
        });

        try {
            requestData(URLs.GET_PRODUCTS.getName(), null, "0", true, true);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.filter_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter){
            popupView = layoutInflater.inflate(R.layout.fragment_products_filter, null);
            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            popupWindow = new PopupWindow(popupView, width, height, true);
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

            categorySpinner = popupView.findViewById(R.id.filterCategorySpinner);
            try {
                requestSpinnerData(URLs.GET_CATEGORIES.getName(), categorySpinner, "category");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final CheckBox activeCheckBox = popupView.findViewById(R.id.activeCheckBox);
            final CheckBox restaurantCheckBox = popupView.findViewById(R.id.restaurantCheckBox);

            filterBtn = popupView.findViewById(R.id.filterBtn);
            filterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productName = ((EditText)popupView.findViewById(R.id.filterProductNameTextEdit)).getText().toString();
                    if (productName.isEmpty()) productName = null;

                    category = ((ItemObject)((ItemAdapter)categorySpinner.getAdapter()).getItem(categorySpinner.getSelectedItemPosition())).getId();
                    active = activeCheckBox.isChecked();
                    restaurant = restaurantCheckBox.isChecked();

                    try {
                        requestData(URLs.GET_PRODUCTS.getName(), productName, category, active, restaurant);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    popupWindow.dismiss();
                }
            });
        }


        return super.onOptionsItemSelected(item);
    }

    private void requestData(final String url, String productName, String category, boolean active, boolean restaurant) throws UnsupportedEncodingException {
        RequestPackage requestPackage = RequestFormer.getRequestPackage(view.getContext(), url, productName, category, active, restaurant);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());
        client.get(getContext(), requestPackage.getFullUrl(), requestPackage.getBytes(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONArray responseArray;
                    if (url.endsWith(URLs.GET_PRODUCTS.getName())){
                        responseArray = new JSONArray(new String(responseBody));
                    }else {
                        JSONObject responseObject = new JSONObject(new String(responseBody));
                        responseArray = (JSONArray)responseObject.get("content");
                    }
                    Log.i("response", responseArray.toString());
                    ProductItemAdapter itemAdapter = new ProductItemAdapter(getContext(), responseArray, R.layout.product_item);
                    productsListView.setAdapter(itemAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Status", statusCode+"");
            }
        });
    }

    private void requestSpinnerData(String url, final Spinner spinner, final String type) throws UnsupportedEncodingException {
        RequestPackage requestPackage = RequestFormer.getRequestPackage(view.getContext(), url);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

        client.get(getContext(), requestPackage.getFullUrl(), requestPackage.getBytes(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONArray responseArray = new JSONArray(new String(responseBody));
                    Log.i("response", responseArray.toString());
                    ItemAdapter itemAdapter = new ItemAdapter(getContext(), responseArray, type, R.layout.spin_item);
                    spinner.setAdapter(itemAdapter);
                    spinner.setSelection(spinner.getAdapter().getCount()-1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Status", statusCode+"");
                ResponseErrorHandler.showErrorMessage(view.getContext(), statusCode);
            }
        });
    }

    private void startIntentLogIn(){
        Intent startIntent = new Intent(getContext(), LogInActivity.class);
        startActivity(startIntent);
    }
}
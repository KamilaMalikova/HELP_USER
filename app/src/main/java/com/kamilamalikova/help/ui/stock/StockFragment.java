package com.kamilamalikova.help.ui.stock;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.kamilamalikova.help.model.FileStream;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.products.adapter.ItemAdapter;
import com.kamilamalikova.help.ui.products.adapter.ItemObject;
import com.kamilamalikova.help.ui.products.adapter.ProductItemAdapter;
import com.kamilamalikova.help.ui.stock.adapter.StockItemAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class StockFragment extends Fragment {

    View view;
    ListView productListView;
    SessionManager sessionManager;

    LayoutInflater layoutInflater;
    SwipeRefreshLayout swipeAndRefresh;
    String productName;
    Spinner categorySpinner;
    public StockFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layoutInflater = inflater;
        view = inflater.inflate(R.layout.fragment_stock, container, false);
        productListView = view.findViewById(R.id.stockProductsListView);
        sessionManager = new SessionManager(view.getContext());
        categorySpinner = view.findViewById(R.id.categorySpinner);
        requestSpinnerData(URLs.GET_CATEGORIES.getName(), categorySpinner, "category");
        //requestData(URLs.GET_ITEMS.getName(), null, "500", "");

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = ((ItemObject)(categorySpinner.getAdapter()).getItem(categorySpinner.getSelectedItemPosition())).getValue();
                String categoryId = ((ItemObject)(categorySpinner.getAdapter()).getItem(categorySpinner.getSelectedItemPosition())).getId();
                requestData(URLs.GET_ITEMS.getName(), productName, categoryId, category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        swipeAndRefresh = view.findViewById(R.id.stockProductsListSwipe);

        swipeAndRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(URLs.GET_ITEMS.getName(), null, "500", "");
                swipeAndRefresh.setRefreshing(false);
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fabAddStockProduct);
        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                try {
                    Navigation.findNavController(view).navigate(R.id.nav_stock_product_add);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    private void requestData(final String url, String productName, String categoryId, String category) {
        RequestPackage requestPackage = RequestFormer.requestStockProductRequestPackage(view.getContext(), url, productName, categoryId, category);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

        client.get(getContext(), requestPackage.getFullUrl(), requestPackage.getBytes(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONArray responseArray;
                    if (url.endsWith(URLs.GET_ITEMS.getName())){
                        responseArray = new JSONArray(new String(responseBody));
                    }else {
                        JSONObject responseObject = new JSONObject(new String(responseBody));
                        responseArray = (JSONArray)responseObject.get("content");
                    }
                    Log.i("response", responseArray.toString());
                    StockItemAdapter itemAdapter = new StockItemAdapter(getContext(), responseArray, "name", R.layout.product_item);
                    productListView.setAdapter(itemAdapter);
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

    private void requestSpinnerData(String url, final Spinner spinner, final String type){
        final RequestPackage requestPackage = new RequestPackage(view.getContext());
        requestPackage.setMethod(RequestType.GET);
        requestPackage.setUrl(url);
        requestPackage.getBytes();

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

        client.get(getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler(){
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
            }
        });
    }

}
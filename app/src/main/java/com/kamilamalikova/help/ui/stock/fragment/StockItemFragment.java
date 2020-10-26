package com.kamilamalikova.help.ui.stock.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.Category;
import com.kamilamalikova.help.model.Product;
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.model.StockItemBalance;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.model.Unit;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.ui.products.adapter.ItemAdapter;
import com.kamilamalikova.help.ui.products.adapter.ItemObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;

public class StockItemFragment extends Fragment {
    View view;
    SessionManager sessionManager;
    EditText productNameEditText;
    Spinner categorySpinner;
    Spinner unitSpinner;
    SwitchCompat restaurantSwitch;

    private String id;
    MenuItem editDone;
    MenuItem edit;
    StockItemBalance product;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_stock_item, container, false);
        sessionManager = new SessionManager(view.getContext());

        categorySpinner = view.findViewById(R.id.detailCategorySpinner);
        unitSpinner = view.findViewById(R.id.detailUnitSpinner);
        productNameEditText = view.findViewById(R.id.detailProductNameTextEdit);
        restaurantSwitch = view.findViewById(R.id.detailRestaurantSwitchCompat);
        //qtyEditText = view.findViewById(R.id.detailQuantityTextNumberDecimal);

        requestSpinnerData(URLs.GET_CATEGORIES.getName(), categorySpinner, "category");
        requestSpinnerData(URLs.GET_UNITS.getName(), unitSpinner, "unitName");


        this.id = getArguments().getString("productId");
        Log.i("ID", this.id);


        enable(false);

        try {
            requestData(URLs.GET_ITEM.getName()+"/"+this.id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_menu, menu);
        editDone = menu.findItem(R.id.edit_done_menu);
        editDone.setVisible(false);
        edit = menu.findItem(R.id.edit_menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit_menu){
            item.setVisible(false);
            enable(true);
            editDone.setVisible(true);
        } else if (item.getItemId() == R.id.edit_done_menu){
            item.setVisible(false);
            StockItemBalance new_product = new StockItemBalance(
                    Long.parseLong(this.id),
                    this.productNameEditText.getText().toString(),
                    new Unit(Integer.parseInt(((ItemObject)((ItemAdapter)unitSpinner.getAdapter()).getItem(unitSpinner.getSelectedItemPosition())).getId()),
                            ((ItemObject)(unitSpinner.getAdapter()).getItem(unitSpinner.getSelectedItemPosition())).getValue()),
                    new Category(Integer.parseInt(((ItemObject)((ItemAdapter)categorySpinner.getAdapter()).getItem(categorySpinner.getSelectedItemPosition())).getId()),
                            ((ItemObject)((ItemAdapter)categorySpinner.getAdapter()).getItem(categorySpinner.getSelectedItemPosition())).getValue()),
                    product.getInStockQty(),
                    product.getProductId(),
                    restaurantSwitch.isChecked()
            );
            saveData(new_product);

        }
        else {
            getActivity().onBackPressed();
        }
        return true;
    }

    private void enable(boolean enabled){
        productNameEditText.setEnabled(enabled);
        restaurantSwitch.setEnabled(enabled);
        unitSpinner.setEnabled(enabled);
        categorySpinner.setEnabled(enabled);
    }

    private void requestSpinnerData(String url, final Spinner spinner, final String type){
        RequestPackage requestPackage = RequestFormer.getRequestPackage(view.getContext(), url);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());
        client.get(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), getString(R.string.content_type), new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONArray responseArray = new JSONArray(new String(responseBody));
                    Log.i("response", responseArray.toString());
                    ItemAdapter itemAdapter = new ItemAdapter(view.getContext(), responseArray, type, R.layout.spin_item);
                    spinner.setAdapter(itemAdapter);
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


    private void saveData(StockItemBalance product){
        RequestPackage requestPackage = null;
        try {
            requestPackage = RequestFormer.getProductRequestPackage(view.getContext(), URLs.POST_ITEM.getName(), product);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());
        client.post(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONObject responseObject = new JSONObject(new String(responseBody));
                    Log.i("Response", responseObject.toString());
                    enable(false);
                    edit.setVisible(true);
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


    private void requestData(String url) throws InterruptedException {
        RequestPackage requestPackage = RequestFormer.getRequestPackage(view.getContext(), url);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());
        client.getThreadPool().awaitTermination(250, TimeUnit.MILLISECONDS);
        client.get(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), getString(R.string.content_type), new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONObject responseObject = new JSONObject(new String(responseBody));
                    Log.i("response", responseObject.toString());
                    product = new StockItemBalance(responseObject);
                    productNameEditText.setText(product.getName());
                    categorySpinner.setSelection(product.getCategory().getId()-1);
                    unitSpinner.setSelection(product.getUnit().getId()-1);
                    restaurantSwitch.setChecked(product.isRestaurant());
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
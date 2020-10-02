package com.kamilamalikova.help.ui.products.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.kamilamalikova.help.LogInActivity;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.FileStream;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.products.adapter.ItemAdapter;
import com.kamilamalikova.help.ui.products.adapter.ItemObject;
import com.kamilamalikova.help.ui.settings.tabfragments.CategorySettingsFragment;
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


public class AddProductFragment extends Fragment {
    volatile Spinner categorySpinner;
    volatile  Spinner unitSpinner;
    volatile  View thisView;
    String unit;
    String category;
    Button saveBtn;
    public AddProductFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);
        this.thisView = view;
        categorySpinner = view.findViewById(R.id.categorySpinner);
        requestData(URLs.GET_CATEGORIES.getName(), categorySpinner, "category");

        unitSpinner = view.findViewById(R.id.unitSpinner);
        requestData(URLs.GET_UNITS.getName(), unitSpinner, "unitName");



        final EditText productNameTextEdit = view.findViewById(R.id.addProductNameTextEdit);
        final EditText costTextEdit = view.findViewById(R.id.costTextNumberDecimal);
        final EditText qtyTextEdit = view.findViewById(R.id.quantityTextNumberDecimal);
        saveBtn = view.findViewById(R.id.addProductSaveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                category = ((ItemObject)((ItemAdapter)categorySpinner.getAdapter()).getItem(categorySpinner.getSelectedItemPosition())).getId();
                unit = ((ItemObject)((ItemAdapter)unitSpinner.getAdapter()).getItem(unitSpinner.getSelectedItemPosition())).getId();
                boolean correct = true;
                if (productNameTextEdit.getText() == null || productNameTextEdit.getText().toString().isEmpty()){
                    correct = false;
                }
                if (costTextEdit.getText() == null || costTextEdit.getText().toString().isEmpty()) costTextEdit.setText("0.00");
                if (qtyTextEdit.getText() == null || qtyTextEdit.getText().toString().isEmpty()) qtyTextEdit.setText("0.00");

                if (correct){
                    addData(productNameTextEdit.getText().toString(),
                            qtyTextEdit.getText().toString(),
                            costTextEdit.getText().toString(),
                            "1",
                            unit,
                            category
                            );
                }else {
                    Toast.makeText(getContext(), getString(R.string.not_all_fields_are_filled), Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        return view;
    }

    private void requestData(String url, final Spinner spinner, final String type){

        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.GET);
        requestPackage.setUrl(url);
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(requestPackage.getJsonObject().toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        Log.i("SER", requestPackage.getFullUrl() + entity);
        Log.i("SER", requestPackage.getFullUrl() + requestPackage.getJsonObject());

        LoggedInUser loggedInUser = new FileStream().readUser(getActivity().getDir("data", Context.MODE_PRIVATE));

        if (loggedInUser == null){
            startIntentLogIn();
            return;
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), loggedInUser.getAuthorizationToken());

        client.get(getContext(), requestPackage.getFullUrl(), entity, entity.getContentType().toString(), new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONArray responseArray = new JSONArray(new String(responseBody));
                    Log.i("Category response", responseArray.toString());
                    ItemAdapter itemAdapter = new ItemAdapter(getContext(), responseArray, type, R.layout.spin_item);
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


    private void addData(String productName, String inStockQty, String cost, String restaurant, String unit, String category){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl("/products");

        requestPackage.setParam("productName", productName);
        requestPackage.setParam("inStockQty", inStockQty);
        requestPackage.setParam("restaurant", restaurant);
        requestPackage.setParam("unit", unit);
        requestPackage.setParam("category", category);
        requestPackage.setParam("cost", cost);

        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(requestPackage.getJsonObject().toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));


        Log.i("SER", requestPackage.getFullUrl() + entity);
        Log.i("SER", requestPackage.getFullUrl() + requestPackage.getJsonObject());

        LoggedInUser loggedInUser = new FileStream().readUser(getActivity().getDir("data", Context.MODE_PRIVATE));

        if (loggedInUser == null){
            startIntentLogIn();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), loggedInUser.getAuthorizationToken());

        final AddProductFragment thisFragment = this;

        client.post(getContext(), requestPackage.getFullUrl(), entity, "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONObject responseObject = new JSONObject(new String(responseBody));
                    Log.i("Product response", responseObject.toString());
                    Navigation.findNavController(thisView).navigate(R.id.nav_products);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Status", statusCode+"");
                if (statusCode == 403){
                    Snackbar.make(getView(), "Необходимо заново авторизоваться", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    startIntentLogIn();
                    return;
                }else {
                    Snackbar.make(getView(), "Неизвестная ошибка! "+statusCode, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            }
        });
    }


    private void startIntentLogIn(){
        Intent startIntent = new Intent(getContext(), LogInActivity.class);
        startActivity(startIntent);
    }
}
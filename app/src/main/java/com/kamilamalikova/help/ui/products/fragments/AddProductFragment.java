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
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.SessionManager;
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
    SessionManager sessionManager;
    View view;
    Spinner categorySpinner;
    Spinner unitSpinner;
    String unit;
    String category;
    Button saveBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_product, container, false);
        sessionManager = new SessionManager(view.getContext());
        categorySpinner = view.findViewById(R.id.categorySpinner);
        requestData(URLs.GET_CATEGORIES.getName(), categorySpinner, "category");

        unitSpinner = view.findViewById(R.id.unitSpinner);
        requestData(URLs.GET_UNITS.getName(), unitSpinner, "unitName");

        final EditText productNameTextEdit = view.findViewById(R.id.addProductNameTextEdit);
        final EditText costTextEdit = view.findViewById(R.id.costTextNumberDecimal);
//        final EditText qtyTextEdit = view.findViewById(R.id.quantityTextNumberDecimal);
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
                //if (qtyTextEdit.getText() == null || qtyTextEdit.getText().toString().isEmpty()) qtyTextEdit.setText("0.00");

                if (correct){
                    addData(productNameTextEdit.getText().toString(),
                            //qtyTextEdit.getText().toString(),
                            costTextEdit.getText().toString(),
                            "1",
                            unit,
                            category
                            );
                }else {
                    Toast.makeText(view.getContext(), getString(R.string.not_all_fields_are_filled), Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        return view;
    }

    private void requestData(String url, final Spinner spinner, final String type){
        RequestPackage requestPackage = RequestFormer.getRequestPackage(view.getContext(), url);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

        client.get(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), getString(R.string.content_type), new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONArray responseArray = new JSONArray(new String(responseBody));
                    Log.i("Category response", responseArray.toString());
                    ItemAdapter itemAdapter = new ItemAdapter(view.getContext(), responseArray, type, R.layout.spin_item);
                    spinner.setAdapter(itemAdapter);
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



    private void addData(String productName, String cost, String restaurant, String unit, String category){
        RequestPackage requestPackage = RequestFormer.getProductRequestPackage(view.getContext(), productName,  cost, restaurant, unit, category);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

        client.post(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONObject responseObject = new JSONObject(new String(responseBody));
                    Log.i("Product response", responseObject.toString());
                    Navigation.findNavController(view).navigate(R.id.nav_products);
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
}
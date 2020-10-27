package com.kamilamalikova.help.ui.stock.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.kamilamalikova.help.LogInActivity;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.Category;
import com.kamilamalikova.help.model.FileStream;
import com.kamilamalikova.help.model.Keyboard;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.model.StockItemBalance;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.model.Unit;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.products.adapter.ItemAdapter;
import com.kamilamalikova.help.ui.products.adapter.ItemObject;
import com.kamilamalikova.help.ui.products.fragments.AddProductFragment;
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


public class AddStockProductFragment extends Fragment {
    SessionManager sessionManager;

    View view;
    Spinner categorySpinner;
    Spinner unitSpinner;
    Unit unit;
    Category category;
    Button saveBtn;
    SwitchCompat restaurant;

    public AddStockProductFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_stock_product, container, false);
        sessionManager = new SessionManager(view.getContext());
        categorySpinner = view.findViewById(R.id.categoryStockSpinner);
        requestData(URLs.GET_CATEGORIES.getName(), categorySpinner, "category");

        unitSpinner = view.findViewById(R.id.unitStockSpinner);
        requestData(URLs.GET_UNITS.getName(), unitSpinner, "unitName");

        restaurant = view.findViewById(R.id.addStockRestaurantSwitchCompat);

        final EditText productNameTextEdit = view.findViewById(R.id.addStockProductNameTextEdit);

        saveBtn = view.findViewById(R.id.addStockProductSaveBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                Keyboard.hideKeyboard(view.getContext());
                category = new Category( Integer.parseInt(((ItemObject)((ItemAdapter)categorySpinner.getAdapter()).getItem(categorySpinner.getSelectedItemPosition())).getId()),
                        ((ItemObject)((ItemAdapter)categorySpinner.getAdapter()).getItem(categorySpinner.getSelectedItemPosition())).getValue()
                        );

                unit = new Unit(
                    Integer.parseInt(
                            ((ItemObject)((ItemAdapter)unitSpinner.getAdapter()).getItem(unitSpinner.getSelectedItemPosition())).getId()
                    ),
                        ((ItemObject)((ItemAdapter)unitSpinner.getAdapter()).getItem(unitSpinner.getSelectedItemPosition())).getValue()
                );

                boolean correct = true;
                if (productNameTextEdit.getText() == null || productNameTextEdit.getText().toString().isEmpty()){
                    correct = false;
                }
                if (((ItemObject)categorySpinner.getSelectedItem()).getId().equals("500") ) correct = false;
                if (((ItemObject)unitSpinner.getSelectedItem()).getId().equals("500") ) correct = false;

                if (correct){
                    StockItemBalance stockItemBalance = new StockItemBalance(0, productNameTextEdit.getText().toString(), unit, category, 0.0, 0, restaurant.isChecked());
                    addData(stockItemBalance);

                }else {
                    Toast.makeText(view.getContext(), getString(R.string.not_all_fields_are_filled), Toast.LENGTH_SHORT)
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
                ResponseErrorHandler.showErrorMessage(view.getContext(), statusCode);
            }
        });
    }

    private void addData(StockItemBalance itemBalance){
        try {
            RequestPackage requestPackage = RequestFormer.getStockItemRequestPackage(view.getContext(), URLs.POST_ITEMS.getName(), itemBalance);
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

            client.post(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i("Status", statusCode+"");
                    try {
                        JSONObject responseObject = new JSONObject(new String(responseBody));
                        Log.i("Stock response", responseObject.toString());
                        Navigation.findNavController(view).navigate(R.id.nav_stock);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
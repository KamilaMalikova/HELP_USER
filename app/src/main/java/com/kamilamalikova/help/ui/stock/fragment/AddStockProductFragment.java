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
import com.kamilamalikova.help.model.LoggedInUser;
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
                    Log.i("response", responseArray.toString());
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

    private void addData(StockItemBalance itemBalance){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(URLs.POST_ITEMS.getName());

        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(requestPackage.getJsonObject(itemBalance).toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException | JSONException e) {
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

        final AddStockProductFragment thisFragment = this;

        client.post(getContext(), requestPackage.getFullUrl(), entity, "application/json", new AsyncHttpResponseHandler(){
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
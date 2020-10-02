package com.kamilamalikova.help.ui.products;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamilamalikova.help.LogInActivity;
import com.kamilamalikova.help.NavigationActivity;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.FileStream;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.products.adapter.ItemAdapter;
import com.kamilamalikova.help.ui.products.adapter.ItemObject;
import com.kamilamalikova.help.ui.products.adapter.ProductItemAdapter;
import com.kamilamalikova.help.ui.products.fragments.AddProductFragment;
import com.kamilamalikova.help.ui.products.fragments.ProductDetailsFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class ProductsFragment extends Fragment {

    volatile LayoutInflater layoutInflater;

    volatile View thisView;

    volatile View popupView;

    volatile PopupWindow popupWindow;

    volatile ListView productsListView;

    String productName;
    String category;
    boolean active;
    boolean restaurant;

    Button filterBtn;

    Spinner categorySpinner;
    public ProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        layoutInflater = inflater;
        final View view = inflater.inflate(R.layout.fragment_products, container, false);
        thisView = view;
        productsListView = view.findViewById(R.id.productsListView);

        final SwipeRefreshLayout swipeView = view.findViewById(R.id.productsListScroll);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(URLs.GET_PRODUCTS.getName()+"/1", null, "0", true, true);
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
                ProductDetailsFragment fragment = new ProductDetailsFragment();
                
            }
        });
        requestData(URLs.GET_PRODUCTS.getName()+"/1", null, "0", true, true);
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
            int height = LinearLayout.LayoutParams.MATCH_PARENT;

            popupWindow = new PopupWindow(popupView, width, height, true);

            categorySpinner = popupView.findViewById(R.id.filterCategorySpinner);
            requestSpinnerData(URLs.GET_CATEGORIES.getName(), categorySpinner, "category");
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

                    requestData(URLs.GET_PRODUCTS.getName()+"/1", productName, category, active, restaurant);
                    popupWindow.dismiss();
                }
            });

            popupWindow.showAtLocation(thisView, Gravity.CENTER, 0, 0);
            popupView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popupWindow.dismiss();
                    return true;
                }
            });
        }


        return super.onOptionsItemSelected(item);
    }


    private void requestData(String url, String productName, String category, boolean active, boolean restaurant){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.GET);
        requestPackage.setUrl(url);

        if (productName != null) requestPackage.setParam("productName", productName);
        if (!category.equals("0")) requestPackage.setParam("category", category);
        if (!active) requestPackage.setParam("active", "0");
        if (!restaurant) requestPackage.setParam("restaurant", "0");

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
                    JSONObject responseObject = new JSONObject(new String(responseBody));
                    JSONArray responseArray = (JSONArray)responseObject.get("content");
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



    private void requestSpinnerData(String url, final Spinner spinner, final String type){
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

    private void startIntentLogIn(){
        Intent startIntent = new Intent(getContext(), LogInActivity.class);
        startActivity(startIntent);
    }
}
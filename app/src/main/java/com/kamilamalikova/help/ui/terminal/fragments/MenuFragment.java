package com.kamilamalikova.help.ui.terminal.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.EatingPlace;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.Order;
import com.kamilamalikova.help.model.Product;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.terminal.adapter.MenuAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class MenuFragment extends Fragment {
    Order order;
    EatingPlace eatingPlace;
    View view;
    ExpandableListView menuListView;
    SwipeRefreshLayout menuSwipeRefresh;
    public Button orderBtn;
    public Set<Product> orderedProducts = new LinkedHashSet<>();
    public MenuFragment thisFragment = this;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            this.order = getArguments().getParcelable("order");
            this.eatingPlace = getArguments().getParcelable("table");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_menu, container, false);
        menuListView = view.findViewById(R.id.menuListView);
        orderBtn = view.findViewById(R.id.orderBtn);
        TextView orderNumTextView = view.findViewById(R.id.orderNumTextView);
        if (order != null) orderNumTextView.setText((getString(R.string.this_order)+" № "+order.getOrderId()));
        else orderNumTextView.setText(getString(R.string.this_order));

        menuSwipeRefresh = view.findViewById(R.id.menuSwipeRefresh);
        menuSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(URLs.GET_MENU_ORDER.getName());
                menuSwipeRefresh.setRefreshing(false);
            }
        });

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderedProducts.size() == 0) {
                    Toast.makeText(getContext(), "Необходимо заказать!", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                if (order == null){
                    createOrder(URLs.POST_ORDERS.getName(), eatingPlace);
                }else {
                    navigate(order);
                }
            }
        });

        requestData(URLs.GET_MENU_ORDER.getName());
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.cancel_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cancel){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(R.string.cancel_order)
                    .setCancelable(true)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (order == null){
                                closeOrder(URLs.POST_TABLE.getName()+"/"+eatingPlace.getId());
                            }
                            else {
                                getActivity().onBackPressed();
                            }
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void requestData(final String url){
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

        LoggedInUser loggedInUser = LoggedInUser.isLoggedIn(getContext(), getActivity());

        AsyncHttpClient client = new AsyncHttpClient();
        assert loggedInUser != null;
        client.addHeader(getString(R.string.authorizationToken), loggedInUser.getAuthorizationToken());

        client.get(getContext(), requestPackage.getFullUrl(), entity, entity.getContentType().toString(), new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody));
                    Log.i("response", response.toString());
                    MenuAdapter adapter = new MenuAdapter(getContext(), response, thisFragment);
                    menuListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
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

    public void createOrder(final String url, EatingPlace eatingPlace){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url);
        requestPackage.setParam("tableId", eatingPlace.getId()+"");
        requestPackage.setParam("username", eatingPlace.getWaiterUsername());
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(requestPackage.getJsonObject().toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        Log.i("SER", requestPackage.getFullUrl() + entity);
        Log.i("SER", requestPackage.getFullUrl() + requestPackage.getJsonObject());

        LoggedInUser loggedInUser = LoggedInUser.isLoggedIn(getContext(), getActivity());

        AsyncHttpClient client = new AsyncHttpClient();
        assert loggedInUser != null;
        client.addHeader(getString(R.string.authorizationToken), loggedInUser.getAuthorizationToken());

        client.post(getContext(), requestPackage.getFullUrl(), entity, "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody));
                    Log.i("response", response.toString());
                    order = new Order(response);
                    navigate(order);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Status", statusCode+"! "+new String(responseBody));
            }
        });
    }


    public void closeOrder(String url ){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url);
        requestPackage.setParam("reserved", "0");
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(requestPackage.getJsonObject().toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        Log.i("SER", requestPackage.getFullUrl() + entity);
        Log.i("SER", requestPackage.getFullUrl() + requestPackage.getJsonObject());

        LoggedInUser loggedInUser = LoggedInUser.isLoggedIn(getContext(), getActivity());

        AsyncHttpClient client = new AsyncHttpClient();
        assert loggedInUser != null;
        client.addHeader(getString(R.string.authorizationToken), loggedInUser.getAuthorizationToken());

        client.post(getContext(), requestPackage.getFullUrl(), entity, "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody));
                    Log.i("response", response.toString());
                    Navigation.findNavController(view).navigate(R.id.nav_terminal);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Status", statusCode+"! "+new String(responseBody));
                Toast.makeText(getContext(), statusCode+"!"+new String(responseBody), Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void navigate(Order order){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("orderedProducts", new ArrayList<>(orderedProducts));
        bundle.putParcelable("order", order);
        Navigation.findNavController(view).navigate(R.id.nav_approve_order, bundle);
    }
}
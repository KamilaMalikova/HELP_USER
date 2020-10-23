package com.kamilamalikova.help.ui.terminal.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kamilamalikova.help.NavigationActivity;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.EatingPlace;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.Order;
import com.kamilamalikova.help.model.OrderDetail;
import com.kamilamalikova.help.model.OrderStatus;
import com.kamilamalikova.help.model.Product;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.terminal.adapter.OrderAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class OrderFragment extends Fragment {

    Order order;
    public Set<Product> newOrderDetails;
    TextView orderNumberTextView;
    TextView sumTextView;
    ListView orderProductListView;
    View view;
    OrderAdapter adapter;

    public Button openMenuBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            order = getArguments().getParcelable("order");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_fragment, container, false);
        orderNumberTextView = view.findViewById(R.id.orderNumberTextView);
        orderProductListView = view.findViewById(R.id.orderProductListView);
        sumTextView = view.findViewById(R.id.sumNumberTextView);
        adapter = new OrderAdapter(getContext(), order, this);
        orderProductListView.setAdapter(adapter);

        orderNumberTextView.setText((getString(R.string.this_order) +" № "+order.getOrderId()));
        openMenuBtn = view.findViewById(R.id.openMenuBtn);
        openMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newOrderDetails != null && newOrderDetails.size() > 0){
                    createOrderDetails(URLs.POST_ORDER_DETAIL.getName()+"/"+order.getOrderId(), newOrderDetails);
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("order", order);
                    Navigation.findNavController(view).navigate(R.id.nav_menu, bundle);
                }
            }
        });
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
            closeOrder(URLs.POST_ORDER.getName(), order.getOrderId());
        }
        return super.onOptionsItemSelected(item);
    }


    public void closeOrder(final String url, long orderId){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url+"/"+orderId);
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
//                    Toast.makeText(getContext(), "Заказ завершен, возьмите чек", Toast.LENGTH_LONG)
//                            .show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    builder.setMessage(R.string.move_to_terminal)
                            .setCancelable(true)
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    navigate();
                                }
                            })
                            .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    if (order.getOrderStatus().equals(OrderStatus.CLOSED)){
                                        openMenuBtn.setText("Терминал");
                                        openMenuBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                navigate();
                                            }
                                        });
                                    }
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

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


    public void createOrderDetails(final String url, Set<Product> products){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url);

        final List<Product> productsToSave = new ArrayList<>();
        for (Product product: products) {
            if (product.getBuyQty() != 0.0) productsToSave.add(product);
        }

        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(requestPackage.getOrderDetailJSONArray(order, productsToSave).toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException | JSONException e) {
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
                    newOrderDetails = null;
                    openMenuBtn.setText(getString(R.string.menu));
                    adapter.setOrder(order);
                    adapter.notifyDataSetChanged();
                    newOrderDetails = null;
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

    private void navigate() {
        Navigation.findNavController(view).navigate(R.id.nav_terminal);
    }

}
package com.kamilamalikova.help.ui.terminal.fragments;

import android.app.AlertDialog;
import android.content.ContentProvider;
import android.content.Context;
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
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.SessionManager;
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
    SessionManager sessionManager;
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
        //Init
        view = inflater.inflate(R.layout.fragment_order_fragment, container, false);
        orderNumberTextView = view.findViewById(R.id.orderNumberTextView);
        orderProductListView = view.findViewById(R.id.orderProductListView);
        sumTextView = view.findViewById(R.id.sumNumberTextView);
        adapter = new OrderAdapter(view.getContext(), order, this);
        orderProductListView.setAdapter(adapter);
        sessionManager = new SessionManager(view.getContext());

        orderNumberTextView.setText((getString(R.string.this_order) +" № "+order.getOrderId()));
        openMenuBtn = view.findViewById(R.id.openMenuBtn);
        openMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newOrderDetails != null && newOrderDetails.size() > 0){
                    try {
                        createOrderDetails(URLs.POST_ORDER_DETAIL.getName()+"/"+order.getOrderId(), newOrderDetails);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
            try {
                closeOrder(URLs.POST_ORDER.getName(), order.getOrderId());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }



    public void closeOrder(final String url, long orderId) throws UnsupportedEncodingException {
        RequestPackage requestPackage = RequestFormer.getRequestPackage(view.getContext(), url, orderId);

        AsyncHttpClient client = new AsyncHttpClient();

        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

        client.post(view.getContext(), requestPackage.getFullUrl(), requestPackage.getBytes(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody));
                    Log.i("response", response.toString());
                    order = new Order(response);

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

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
                ResponseErrorHandler.showErrorMessage(view.getContext(), statusCode);
            }
        });
    }



    public void createOrderDetails(final String url, Set<Product> products) throws JSONException {
        RequestPackage requestPackage = RequestFormer.getRequestPackage(view.getContext(), url, order, products);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

        client.post(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody));
                    Log.i("response", response.toString());
                    order = new Order(response);
                    newOrderDetails = null;
                    openMenuBtn.setText(getString(R.string.menu));
                    adapter.setOrder(order);
                    newOrderDetails = null;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Error", statusCode+" "+new String(responseBody));
                ResponseErrorHandler.showErrorMessage(view.getContext(), statusCode);
            }
        });
    }

    private void navigate() {
        Navigation.findNavController(view).navigate(R.id.nav_terminal);
    }

}
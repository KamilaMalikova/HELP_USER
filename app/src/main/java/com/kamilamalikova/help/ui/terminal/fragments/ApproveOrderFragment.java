package com.kamilamalikova.help.ui.terminal.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.EatingPlace;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.Order;
import com.kamilamalikova.help.model.Product;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.terminal.adapter.OrderDetailAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class ApproveOrderFragment extends Fragment {
    SessionManager sessionManager;

    ArrayList<Product> orderedProducts;
    ListView orderProductListView;
    Order order;
    View view;
    TextView orderNumberTextView;
    OrderDetailAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderedProducts = getArguments().getParcelableArrayList("orderedProducts");
            order = getArguments().getParcelable("order");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Init objects
        view = inflater.inflate(R.layout.fragment_approve_order, container, false);
        sessionManager = new SessionManager(view.getContext());
        orderProductListView = view.findViewById(R.id.orderProductListView);
        adapter = new OrderDetailAdapter(orderedProducts, view.getContext(), this);
        orderNumberTextView = view.findViewById(R.id.orderNumberTextView);
        Button approveOrderBtn = view.findViewById(R.id.approveOrderBtn);

        //Set order number
        orderNumberTextView.setText((getString(R.string.order)+" №"+order.getOrderId()));

        orderProductListView.setAdapter(adapter);

        approveOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createOrderDetails(URLs.POST_ORDER_DETAIL.getName()+"/"+order.getOrderId(), order, orderedProducts);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }

    public RequestPackage getRequestPackage(String url, Order order, List<Product> products) throws JSONException {
        RequestPackage requestPackage = new RequestPackage(view.getContext());
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url);
        List<Product> productsToSave = new ArrayList<>();
        for (Product product: products) {
            if (product.getBuyQty() > 0.0) productsToSave.add(product);
        }
        Log.i("Products", requestPackage.getOrderDetailJSONArray(order, productsToSave).toString());
        requestPackage.setEntity(requestPackage.getOrderDetailJSONArray(order, productsToSave).toString());
        return requestPackage;
    }

    public void createOrderDetails(final String url, Order order, List<Product> products) throws JSONException {
        RequestPackage requestPackage = getRequestPackage(url, order, products);

        AsyncHttpClient client = new AsyncHttpClient();

        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

        client.post(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody));
                    Log.i("response", response.toString());
                    Order new_order = new Order(response);
                    navigate(new_order);
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

    private void navigate(Order order){
        Bundle bundle = new Bundle();
        bundle.putParcelable("order", order);
        Navigation.findNavController(view).navigate(R.id.nav_order, bundle);
    }
}
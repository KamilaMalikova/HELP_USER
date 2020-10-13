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

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class ApproveOrderFragment extends Fragment {
    public ArrayList<Product> orderedProducts;
    ListView orderProductListView;
    public Order order;
    View view;
    TextView orderNumberTextView;
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

        view = inflater.inflate(R.layout.fragment_approve_order, container, false);
        orderProductListView = view.findViewById(R.id.orderProductListView);
        OrderDetailAdapter adapter = new OrderDetailAdapter(orderedProducts, getContext(), this);
        orderProductListView.setAdapter(adapter);
        orderNumberTextView = view.findViewById(R.id.orderNumberTextView);

        orderNumberTextView.setText((getString(R.string.order)+" №"+order.getOrderId()));
        Button approveOrderBtn = view.findViewById(R.id.approveOrderBtn);
        approveOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrderDetails(URLs.POST_ORDER_DETAIL.getName()+"/"+order.getOrderId(), order, orderedProducts);
            }
        });
        return view;
    }

    public void createOrderDetails(final String url, Order order, List<Product> products){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url);

        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(requestPackage.getOrderDetailJSONArray(order, products).toString().getBytes("UTF-8"));
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
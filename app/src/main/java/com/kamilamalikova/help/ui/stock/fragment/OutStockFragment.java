package com.kamilamalikova.help.ui.stock.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.DOCTYPE;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.stock.adapter.StockDocAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class OutStockFragment extends Fragment {

    ExpandableListView outStockListView;

    public OutStockFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_out_stock, container, false);

        outStockListView = view.findViewById(R.id.outStockListView);
        requestData(URLs.GET_DOCS.getName()+"/1", DOCTYPE.OUT.getName(), null, null);

        final SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.outStockSwipe);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(URLs.GET_DOCS.getName()+"/1", DOCTYPE.OUT.getName(), null, null);
                swipeRefresh.setRefreshing(false);
            }
        });
        return view;
    }


    private void requestData(final String url, String type, LocalDateTime from, LocalDateTime to){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.GET);
        requestPackage.setUrl(url);

        if (type != null) requestPackage.setParam("type", type);
        if (from != null) requestPackage.setParam("from", from.toString());
        if (to != null) requestPackage.setParam("to", to.toString());

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
                Log.i("Status", statusCode+" out");
                try {
                    JSONArray responseArray;
                    if (url.endsWith(URLs.GET_ITEMS.getName())){
                        responseArray = new JSONArray(new String(responseBody));
                    }else {
                        JSONObject responseObject = new JSONObject(new String(responseBody));
                        responseArray = (JSONArray)responseObject.get("content");
                    }
                    Log.i("response", responseArray.toString());
                    StockDocAdapter docAdapter = new StockDocAdapter(getContext(), responseArray);
                    outStockListView.setAdapter(docAdapter);
                    docAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i(statusCode+"", new String(responseBody));
            }
        });
    }
}
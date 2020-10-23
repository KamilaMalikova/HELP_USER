package com.kamilamalikova.help.ui.stock.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.DOCTYPE;
import com.kamilamalikova.help.model.FileStream;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.Role;
import com.kamilamalikova.help.model.StartIntent;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.products.adapter.ProductItemAdapter;
import com.kamilamalikova.help.ui.stock.adapter.StockDocAdapter;
import com.kamilamalikova.help.ui.users.listener.PagginationLinerScrollListener;
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


public class InStockFragment extends Fragment {

    View view;
    LocalDateTime to;
    LocalDateTime from;
    ExpandableListView inStockListView;

    StockDocAdapter docAdapter;
    private static final int PAGE_START = 0;
    private int currentPage = PAGE_START;

    public InStockFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AndroidThreeTen.init(getContext());
        to = LocalDateTime.now();
        from = LocalDateTime.of(to.getYear(), to.getMonth().getValue()-1, to.getDayOfMonth(), 0, 0);

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_in_stock, container, false);
        inStockListView = view.findViewById(R.id.inStockListView);
        requestData(URLs.GET_DOCS.getName()+"/"+currentPage, DOCTYPE.IN.getName(), from, to);

        docAdapter = new StockDocAdapter(getContext());

        final SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.inStockSwipe);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                docAdapter.init();
                requestData(URLs.GET_DOCS.getName()+"/"+currentPage, DOCTYPE.IN.getName(), from, to);
                swipeRefresh.setRefreshing(false);
            }
        });
        return view;
    }


    public void requestData(final String url, String type, LocalDateTime from, LocalDateTime to){
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
                Log.i("Status", statusCode+" in");
                try {
                    JSONArray responseArray;
//                    if (url.endsWith(URLs.GET_ITEMS.getName())){
//                        responseArray = new JSONArray(new String(responseBody));
//                    }else {
//                        JSONObject responseObject = new JSONObject(new String(responseBody));
//                        responseArray = (JSONArray)responseObject.get("content");
//                    }
                    responseArray = new JSONArray(new String(responseBody));
                    Log.i("response", responseArray.toString());
                    docAdapter.init();
                    docAdapter.add(responseArray);
                    inStockListView.setAdapter(docAdapter);
                    docAdapter.notifyDataSetChanged();
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

}
package com.kamilamalikova.help.ui.stock.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.kamilamalikova.help.LogInActivity;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.DOCTYPE;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.SessionManager;
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
    SessionManager sessionManager;
    View view;
    ExpandableListView outStockListView;

    LocalDateTime to;
    LocalDateTime from;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_out_stock, container, false);
        sessionManager = new SessionManager(view.getContext());
        AndroidThreeTen.init(view.getContext());
        to = LocalDateTime.now();
        from = LocalDateTime.of(to.getYear(), to.getMonth().getValue()-1, to.getDayOfMonth(), 0, 0);
        outStockListView = view.findViewById(R.id.outStockListView);
        requestData(URLs.GET_DOCS.getName()+"/0", DOCTYPE.OUT.getName(), from, to);


        final SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.outStockSwipe);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData(URLs.GET_DOCS.getName()+"/0", DOCTYPE.OUT.getName(), from, to);
                swipeRefresh.setRefreshing(false);
            }
        });
        return view;
    }


    public void requestData(final String url, String type, LocalDateTime from, LocalDateTime to) {
        RequestPackage requestPackage = RequestFormer.getFilterRequestPackage(view.getContext(), url, type, from, to);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

        client.get(view.getContext(), requestPackage.getFullUrl(), requestPackage.getBytes(), getString(R.string.content_type), new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+" out");
                try {
                    JSONArray responseArray;
                    responseArray = new JSONArray(new String(responseBody));
                    Log.i("response", responseArray.toString());
                    StockDocAdapter docAdapter = new StockDocAdapter(view.getContext(), responseArray);
                    outStockListView.setAdapter(docAdapter);
                    docAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i(statusCode+"", new String(responseBody));
                ResponseErrorHandler.showErrorMessage(view.getContext(), statusCode);
            }
        });
    }
}
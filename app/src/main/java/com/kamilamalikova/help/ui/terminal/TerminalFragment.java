package com.kamilamalikova.help.ui.terminal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.EatingPlace;
import com.kamilamalikova.help.ui.terminal.listeners.PaginationScrollListener;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.terminal.adapter.TableAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class TerminalFragment extends Fragment {

    RecyclerView tablesList;
    ProgressBar progressBar;

    TableAdapter adapter;
    GridLayoutManager gridLayoutManager;

    SwipeRefreshLayout swipeRefreshLayout;

    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 5;
    private int currentPage = PAGE_START;

    public TerminalFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_terminal, container, false);

        tablesList = view.findViewById(R.id.dataList);
        progressBar = view.findViewById(R.id.tablesProgressBar);

        swipeRefreshLayout = view.findViewById(R.id.terminalSwipeRefresh);

        gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        adapter = new TableAdapter(getContext(), getActivity());

        tablesList.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                requestData(URLs.GET_TABLES.getName()+"/"+currentPage);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        requestData(URLs.GET_TABLES.getName()+"/"+currentPage);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoading = false;
                isLastPage = false;
                TOTAL_PAGES = 5;
                currentPage = PAGE_START;
                swipeRefreshLayout.setRefreshing(false);
                adapter.setEatingPlaceList(new ArrayList<EatingPlace>());
                requestData(URLs.GET_TABLES.getName()+"/"+currentPage);
            }
        });

        return view;
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
                Log.i("Status", statusCode+" in");
                try {

                    JSONArray responseArray;
                    if (url.endsWith(URLs.GET_ITEMS.getName())){
                        responseArray = new JSONArray(new String(responseBody));
                        TOTAL_PAGES = 1;
                        isLastPage = true;
                    }else {
                        JSONObject responseObject = new JSONObject(new String(responseBody));
                        Log.i("response", responseObject.toString());
                        TOTAL_PAGES = responseObject.getInt("totalPages");
                        //currentPage = responseObject.getInt("number");
                        isLastPage = responseObject.getBoolean("last");
                        responseArray = (JSONArray)responseObject.get("content");
                    }
                    Log.i("response", responseArray.toString());
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;
                    adapter.add(responseArray);
                    tablesList.setLayoutManager(gridLayoutManager);
                    tablesList.setAdapter(adapter);
                    if (!isLastPage) adapter.addLoadingFooter();
                    else adapter.removeLoadingFooter();

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
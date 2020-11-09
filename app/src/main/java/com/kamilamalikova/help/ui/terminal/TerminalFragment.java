package com.kamilamalikova.help.ui.terminal;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.DOCTYPE;
import com.kamilamalikova.help.model.EatingPlace;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.model.TableType;
import com.kamilamalikova.help.ui.terminal.adapter.TableFilterAdapter;
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
import org.threeten.bp.LocalDateTime;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.zip.Inflater;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class TerminalFragment extends Fragment {

    SessionManager sessionManager;
    LoggedInUser loggedInUser;

    RecyclerView tablesList;
    ProgressBar progressBar;
    TableAdapter adapter;
    GridLayoutManager gridLayoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    View view;
    Spinner tableTypeSpinner;
    TableFilterAdapter filterAdapter;

    boolean all = true;
    boolean reserved = false;
    boolean my = false;

    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 5;
    private int currentPage = PAGE_START;
    private LayoutInflater layoutInflater;
    private PopupWindow popupWindow;

    public TerminalFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Init view
        view = inflater.inflate(R.layout.fragment_terminal, container, false);

        sessionManager = new SessionManager(view.getContext());
        loggedInUser = new LoggedInUser(sessionManager);

        this.layoutInflater = inflater;
        // Init widgets
        tablesList = view.findViewById(R.id.dataList);
        progressBar = view.findViewById(R.id.tablesProgressBar);
        swipeRefreshLayout = view.findViewById(R.id.terminalSwipeRefresh);
        //Init Tables GridLayoutManager
        gridLayoutManager = new GridLayoutManager(view.getContext(), 2);
        //Init tables adapter
        adapter = new TableAdapter(view.getContext(), getActivity());
        //Set layout manager and adapter
        tablesList.setAdapter(adapter);
        tablesList.setLayoutManager(gridLayoutManager);

        // Set on scroll listener for pagination
        tablesList.addOnScrollListener(new PaginationScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                try {
                    requestData(URLs.GET_TABLES.getName()+"/"+currentPage, all, reserved, my);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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

        // Request table data for the first time
        try {
            requestData(URLs.GET_TABLES.getName()+"/"+currentPage, all, reserved, my);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Set refreshing listener. Configure request params as default
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoading = false;
                isLastPage = false;
                TOTAL_PAGES = 5;
                currentPage = PAGE_START;
                swipeRefreshLayout.setRefreshing(false);
                adapter.setEatingPlaceList(new ArrayList<EatingPlace>());
                all = true;
                reserved = false;
                my = false;
                try {
                    requestData(URLs.GET_TABLES.getName()+"/"+currentPage, true, false, false);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Set filter menu
        inflater.inflate(R.menu.filter_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Filter clicked
        if (item.getItemId() == R.id.filter){
            View popupView = layoutInflater.inflate(R.layout.tables_filter, null);
            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;

            popupWindow = new PopupWindow(popupView, width, height, true);
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            //popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setFocusable(true);

            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
            tableTypeSpinner = popupView.findViewById(R.id.tableTypeSpinner);
            filterAdapter = new TableFilterAdapter(view.getContext());
            tableTypeSpinner.setAdapter(filterAdapter);

            final SwitchCompat iReserveSwitch = popupView.findViewById(R.id.iReserveSwitch);

            Button filterBtn = popupView.findViewById(R.id.filterTablesBtn);

            filterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch ((TableType)tableTypeSpinner.getSelectedItem()){
                        case ALL:
                            all = true;
                            break;
                        case FREE:
                            all = false;
                            my = false;
                            reserved = false;
                            break;
                        case RESERVED:
                            all = false;
                            reserved = true;
                    }
                    if (iReserveSwitch.isChecked()){
                        my = true;
                    }
                    isLoading = false;
                    isLastPage = false;
                    TOTAL_PAGES = 5;
                    currentPage = PAGE_START;
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.setEatingPlaceList(new ArrayList<EatingPlace>());
                    try {
                        requestData(URLs.GET_TABLES.getName()+"/"+currentPage, all, reserved, my);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    popupWindow.dismiss();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }


    public RequestPackage getRequestPackage(String url, boolean all, boolean reserved, boolean my){
        RequestPackage requestPackage = new RequestPackage(view.getContext());
        requestPackage.setMethod(RequestType.GET);
        requestPackage.setUrl(url);

        if (my) {
            requestPackage.setParam("username", loggedInUser.getUsername());
            requestPackage.setParam("reserved", "1");
        }
        else if (!all){
            if (reserved) requestPackage.setParam("reserved", "1");
            else requestPackage.setParam("reserved", "0");
        }
        return requestPackage;
    }

    public void requestData(final String url, boolean all, boolean reserved, boolean my) throws UnsupportedEncodingException {
        RequestPackage requestPackage = getRequestPackage(url, all, reserved, my);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), loggedInUser.getAuthorizationToken());
        //Send get request with params
        client.get(view.getContext(), requestPackage.getFullUrl(), requestPackage.getBytes(), "application/json", new AsyncHttpResponseHandler(){
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
                    if (!isLastPage) adapter.addLoadingFooter();
                    else adapter.removeLoadingFooter();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                try {
                    Log.i("Status", statusCode+new String(responseBody));
                    ResponseErrorHandler.showErrorMessage(view.getContext(), statusCode);
                }catch (Exception e){
                    ResponseErrorHandler.showErrorMessage(view.getContext(), 0);
                }

            }

        });
    }

}
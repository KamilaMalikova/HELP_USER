package com.kamilamalikova.help.ui.history;

import android.app.DatePickerDialog;
import android.app.MediaRouteButton;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.EatingPlace;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.OrderStatus;
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.Role;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.model.TableType;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.model.User;
import com.kamilamalikova.help.model.UserRole;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.history.adapter.OrderHistoryAdapter;
import com.kamilamalikova.help.ui.history.adapter.OrderStatusAdapter;
import com.kamilamalikova.help.ui.history.adapter.UsersAdapter;
import com.kamilamalikova.help.ui.history.listener.PaginationOnScrollListener;
import com.kamilamalikova.help.ui.stock.adapter.StockDocAdapter;
import com.kamilamalikova.help.ui.terminal.adapter.OrderAdapter;
import com.kamilamalikova.help.ui.terminal.adapter.TableFilterAdapter;
import com.kamilamalikova.help.ui.users.listener.PagginationLinerScrollListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class OrdersHistoryFragment extends Fragment {
    SessionManager sessionManager;
    LoggedInUser loggedInUser;
    View view;
    View popupView;
    LayoutInflater inflater;

    LocalDateTime to;
    LocalDateTime from;

    ExpandableListView historyListView;

    OrderHistoryAdapter adapter;

    SwipeRefreshLayout swipeRefreshLayout;

    UsersAdapter usersAdapter;

    Spinner filterOrderStatusSpinner;
    AppCompatAutoCompleteTextView orderWaiterNameTextView;

    TextView startDateDisplay;
    TextView endDateDisplay;

    DatePickerDialog.OnDateSetListener mDateStartSetListener;
    DatePickerDialog.OnDateSetListener mDateEndSetListener;

    User user;
    OrderStatus orderStatus = OrderStatus.ALL;

    OrderStatusAdapter orderStatusAdapter;

    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 5;
    private int currentPage = PAGE_START;

    private LinearLayoutManager layoutManager;
    private ProgressBar progressBar;
    private PopupWindow popupWindow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        view = inflater.inflate(R.layout.fragment_orders_history, container, false);
        sessionManager = new SessionManager(view.getContext());
        loggedInUser = new LoggedInUser(sessionManager);
        AndroidThreeTen.init(view.getContext());
        to = LocalDateTime.now();
        from = LocalDateTime.of(to.getYear(), to.getMonth().getValue()-1, to.getDayOfMonth(), 0, 0);
        historyListView = view.findViewById(R.id.orderHistoryListView);
        adapter = new OrderHistoryAdapter(view.getContext());
        progressBar = view.findViewById(R.id.orderHistoryProgressBar);
        swipeRefreshLayout = view.findViewById(R.id.orderHistorySwipeRefresh);
        historyListView.setAdapter(adapter);

        orderStatusAdapter = new OrderStatusAdapter(view.getContext());

        if (loggedInUser.getRole() == UserRole.ADMIN || loggedInUser.getRole() == UserRole.OWNER){
            requestData(URLs.GET_USERS.getName());
        }else {
                user = new User(loggedInUser);
        }

        requestData(URLs.GET_ORDERS.getName()+"/"+currentPage,
                null,
                (user != null) ? user.getUsername() : null, from, to, 0);


        layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        historyListView.setOnScrollListener(new PaginationOnScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                if (currentPage < TOTAL_PAGES) requestData(URLs.GET_ORDERS.getName()+"/"+currentPage,
                        orderStatus,
                        (user != null) ? user.getUsername() : null,
                        from, to, 0);
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoading = false;
                isLastPage = false;
                TOTAL_PAGES = 5;
                currentPage = PAGE_START;
                adapter.init();
                to = LocalDateTime.now();
                from = LocalDateTime.of(to.getYear(), to.getMonth().getValue()-1, to.getDayOfMonth(), 0, 0);

                requestData(URLs.GET_ORDERS.getName()+"/"+currentPage,
                        null,
                        (loggedInUser.isOwnerOrAdmin()) ? null: loggedInUser.getUsername(), from, to, 0);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.filter_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter){
            popupView = inflater.inflate(R.layout.filter_order_history, null);
            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;

            popupWindow = new PopupWindow(popupView, width, height, true);
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);

            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
            filterOrderStatusSpinner = popupView.findViewById(R.id.filterOrderStatusSpinner);

            filterOrderStatusSpinner.setAdapter(orderStatusAdapter);

            orderWaiterNameTextView = popupView.findViewById(R.id.orderWaiterNameTextView);
            if (!loggedInUser.isOwnerOrAdmin()){
                orderWaiterNameTextView.setFocusable(false);
                if (user == null) {
                    user = new User(loggedInUser);
                }
                orderWaiterNameTextView.setText(user.getName());
            }
            orderWaiterNameTextView.setThreshold(1);
            orderWaiterNameTextView.setAdapter(usersAdapter);

            startDateDisplay = popupView.findViewById(R.id.orderDateStartFilterTextView);
            startDateDisplay.setInputType(InputType.TYPE_NULL);

            endDateDisplay = popupView.findViewById(R.id.orderDateEndFilterTextView);
            endDateDisplay.setInputType(InputType.TYPE_NULL);

            startDateDisplay.setText((from.getDayOfMonth()+"/"+(from.getMonthValue()+1)+"/"+from.getYear()));
            endDateDisplay.setText((to.getDayOfMonth()+"/"+(to.getMonthValue()+1)+"/"+to.getYear()));

            startDateDisplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(popupView.getContext(),
                            R.style.Theme_AppCompat_Light_Dialog,
                            mDateStartSetListener,
                            year, month, day);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setGravity(Gravity.CENTER);

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    dialog.show();
                }
            });

            endDateDisplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(popupView.getContext(),
                            R.style.Theme_AppCompat_Light_Dialog,
                            mDateEndSetListener,
                            year, month, day);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setGravity(Gravity.CENTER);

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    dialog.show();
                }
            });

            mDateStartSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month+=1;
                    from = LocalDateTime.of(year, month, dayOfMonth, 0, 0, 0, 0);
                    if (from.compareTo(LocalDateTime.now()) > 0){
                        Toast.makeText(popupView.getContext(), "Начальная дата не может быть больше текущей даты", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    String date = dayOfMonth+"/"+month+"/"+year;
                    startDateDisplay.setText(date);
                }
            };

            mDateEndSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month+=1;
                    to = LocalDateTime.of(year, month, dayOfMonth, 23, 59, 0, 0);
                    if (to.compareTo(from) < 0){
                        Toast.makeText(popupView.getContext(), "Конечная дата не может быть больше начальной", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    String date = dayOfMonth+"/"+month+"/"+year;
                    endDateDisplay.setText(date);
                }
            };

            orderWaiterNameTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    user = usersAdapter.getItem(position);
                }
            });

            Button filterBtn = popupView.findViewById(R.id.orderFilterBtn);

            filterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isLoading = false;
                    isLastPage = false;
                    TOTAL_PAGES = 5;
                    currentPage = PAGE_START;
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.init();
                    requestData(URLs.GET_ORDERS.getName()+"/"+currentPage,
                            ((OrderStatus) orderStatusAdapter.getItem(filterOrderStatusSpinner.getSelectedItemPosition())),
                            (user != null) ? user.getUsername() : null, from, to, 0);
                    popupWindow.dismiss();
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }



    public void requestData(final String url, OrderStatus status, String username, LocalDateTime from, LocalDateTime to, int tableId){
        RequestPackage requestPackage = RequestFormer.getOrdersRequestPackage(view.getContext(), url, status, username, from, to, tableId);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

        client.get(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler(){
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
                        TOTAL_PAGES = responseObject.getInt("totalPages");
                        //currentPage = responseObject.getInt("number");
                        isLastPage = responseObject.getBoolean("last");
                        responseArray = (JSONArray)responseObject.get("content");
                    }
                    Log.i("response", responseArray.toString());
                    progressBar.setVisibility(View.GONE);
                    isLoading = false;
                    adapter.add(responseArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Status", statusCode+"");
                ResponseErrorHandler.showErrorMessage(view.getContext(), statusCode);
            }
        });
    }


    public void requestData(final String url){
        RequestPackage requestPackage = RequestFormer.getRequestPackage(view.getContext(), url);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

        client.get(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONArray responseArray;
                    responseArray = new JSONArray(new String(responseBody));
                    ArrayList<User> users = new ArrayList<>();
                    if (responseArray.length() > 0){
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject object = responseArray.getJSONObject(i);
                            users.add(new User(object));
                        }
                    }
                    usersAdapter = new UsersAdapter(view.getContext(), users);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Status", statusCode+"");
                ResponseErrorHandler.showErrorMessage(view.getContext(), statusCode);
            }

        });
    }
}
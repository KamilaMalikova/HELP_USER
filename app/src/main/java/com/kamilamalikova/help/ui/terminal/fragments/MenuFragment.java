package com.kamilamalikova.help.ui.terminal.fragments;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.EatingPlace;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.Order;
import com.kamilamalikova.help.model.Product;
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.terminal.adapter.MenuAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class MenuFragment extends Fragment {

    SessionManager sessionManager;
    LoggedInUser loggedInUser;

    Order order;
    EatingPlace eatingPlace;
    View view;
    ExpandableListView menuListView;
    SwipeRefreshLayout menuSwipeRefresh;
    SearchView searchMenuView;
    MenuAdapter adapter;
    TextView orderNumTextView;
    public Button orderBtn;
    public Set<Product> orderedProducts = new LinkedHashSet<>();
    public MenuFragment thisFragment = this;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            this.order = getArguments().getParcelable("order");
            this.eatingPlace = getArguments().getParcelable("table");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            //Init parameters
            view = inflater.inflate(R.layout.fragment_menu, container, false);
            sessionManager = new SessionManager(view.getContext());
            loggedInUser = new LoggedInUser(sessionManager);
            menuListView = view.findViewById(R.id.menuListView);
            adapter = new MenuAdapter(view.getContext(), thisFragment);
            orderBtn = view.findViewById(R.id.orderBtn);
            searchMenuView = view.findViewById(R.id.searchMenuView);
            menuSwipeRefresh = view.findViewById(R.id.menuSwipeRefresh);
            orderNumTextView = view.findViewById(R.id.orderNumTextView);
            SearchManager searchManager = (SearchManager) view.getContext().getSystemService(Context.SEARCH_SERVICE);
            //Set adapter on Menu
            menuListView.setAdapter(adapter);
            // Show order number
            if (order != null) orderNumTextView.setText((getString(R.string.this_order)+" № "+order.getOrderId()));
            else orderNumTextView.setText(getString(R.string.this_order));

            // Refreshing menu
            menuSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    requestData(URLs.GET_MENU_ORDER.getName());
                    menuSwipeRefresh.setRefreshing(false);
                }
            });
            // Order selected products
            orderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //1. Check if chosen
                    if (orderedProducts.size() == 0) {
                        Toast.makeText(view.getContext(), "Необходимо заказать!", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    //2. Create order if was not
                    if (order == null){
                        try {
                            createOrder(URLs.POST_ORDERS.getName(), eatingPlace);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    //3. Navigate to approve fragment
                    else {
                        navigate(order);
                    }
                }
            });

            // Config search in menu
            searchMenuView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchMenuView.setIconifiedByDefault(false);
            searchMenuView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    adapter.filter(query);
                    expandAll();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.filter(newText);
                    expandAll();
                    return false;
                }
            });
            // Close search view
            searchMenuView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    adapter.filter("");
                    expandAll();
                    return false;
                }
            });
            // First request
            requestData(URLs.GET_MENU_ORDER.getName());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
    //Close order and free table
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.cancel_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cancel){
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage(R.string.cancel_order)
                    .setCancelable(true)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (order == null){
                                try {
                                    closeOrder(URLs.POST_TABLE.getName()+"/"+eatingPlace.getId());
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                getActivity().onBackPressed();
                            }
                        }
                    })
                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void expandAll() {
        if (menuListView != null){
            int count = adapter.getGroupCount();
            Log.i("count", count+"");
            for (int i = 0; i < count; i++){
                menuListView.expandGroup(i);
            }
        }
    }

    public void requestData(String url){
        RequestPackage requestPackage = RequestFormer.getRequestPackage(view.getContext(), url);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());
        client.get(view.getContext(), requestPackage.getFullUrl(), requestPackage.getBytes(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody));
                    Log.i("response", response.toString());
                    adapter.add(response);
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

    public void createOrder(String url, EatingPlace eatingPlace) throws UnsupportedEncodingException {
        RequestPackage requestPackage = RequestFormer.getRequestPackage(view.getContext(), url, eatingPlace);

        AsyncHttpClient client = new AsyncHttpClient();
        assert loggedInUser != null;
        client.addHeader(getString(R.string.authorizationToken), loggedInUser.getAuthorizationToken());

        client.post(view.getContext(), requestPackage.getFullUrl(), requestPackage.getBytes(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody));
                    Log.i("response", response.toString());
                    order = new Order(response);
                    navigate(order);
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

    public void closeOrder(String url ) throws UnsupportedEncodingException {
        RequestPackage requestPackage = RequestFormer.getRequestPackage(view.getContext(), url, false);

        AsyncHttpClient client = new AsyncHttpClient();
        assert loggedInUser != null;
        client.addHeader(getString(R.string.authorizationToken), loggedInUser.getAuthorizationToken());

        client.post(view.getContext(), requestPackage.getFullUrl(), requestPackage.getBytes(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody));
                    Log.i("response", response.toString());
                    Navigation.findNavController(view).navigate(R.id.nav_terminal);
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
    // Navigate to order approve fragment with order bundle
    private void navigate(Order order){
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("orderedProducts", new ArrayList<>(orderedProducts));
        bundle.putParcelable("order", order);
        Navigation.findNavController(view).navigate(R.id.nav_approve_order, bundle);
    }
}
package com.kamilamalikova.help.ui.users;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.EatingPlace;
import com.kamilamalikova.help.model.Keyboard;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.Role;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.model.User;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.terminal.adapter.TableAdapter;
import com.kamilamalikova.help.ui.terminal.listeners.PaginationScrollListener;
import com.kamilamalikova.help.ui.users.adapter.RoleAdapter;
import com.kamilamalikova.help.ui.users.adapter.UsersAdapter;
import com.kamilamalikova.help.ui.users.listener.PagginationLinerScrollListener;
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

public class UsersFragment extends Fragment {
    SessionManager sessionManager;
    View view;
    RecyclerView usersList;
    ProgressBar progressBar;
    UsersAdapter adapter;
    LinearLayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;
    Spinner filterByRoleSpinner;
    RoleAdapter roleAdapter;
    SearchView usersSearchView;
    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 5;
    private int currentPage = PAGE_START;


    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_users, container, false);
        sessionManager = new SessionManager(view.getContext());
        filterByRoleSpinner = view.findViewById(R.id.filterByRoleSpinner);
        roleAdapter = new RoleAdapter(view.getContext());
        roleAdapter.add(0, Role.ALL);

        filterByRoleSpinner.setAdapter(roleAdapter);
        usersSearchView = view.findViewById(R.id.usersSearchView);

        usersList = view.findViewById(R.id.usersRecycleView);
        progressBar = view.findViewById(R.id.usersProgressBar);

        swipeRefreshLayout = view.findViewById(R.id.usersSwipeRefresh);

        layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        adapter = new UsersAdapter(view.getContext(), view);

        usersList.setLayoutManager(layoutManager);
        usersList.setAdapter(adapter);
        adapter.init();
        usersList.addOnScrollListener(new PagginationLinerScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                Role role = (Role)filterByRoleSpinner.getSelectedItem();
                if (role == Role.ALL) role = null;
                String query = usersSearchView.getQuery().toString();
                if (query.isEmpty()) query = null;
                requestData(URLs.GET_USERS.getName()+"/"+currentPage, query, role);
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
                try {
                    isLoading = false;
                    isLastPage = false;
                    TOTAL_PAGES = 5;
                    currentPage = PAGE_START;
                    //adapter.init();
                    filterByRoleSpinner.setSelection(0);
                    //requestData(URLs.GET_USERS.getName()+"/"+currentPage, null, null);
                    swipeRefreshLayout.setRefreshing(false);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.nav_add_user);
            }
        });

        filterByRoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Role role = (Role)roleAdapter.getItem(position);
                if (role == Role.ALL) role = null;
                isLoading = false;
                isLastPage = false;
                TOTAL_PAGES = 5;
                currentPage = PAGE_START;
                adapter.init();
                requestData(URLs.GET_USERS.getName()+"/"+currentPage, null, role);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SearchManager searchManager = (SearchManager) view.getContext().getSystemService(Context.SEARCH_SERVICE);
        usersSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        usersSearchView.setIconifiedByDefault(false);
        usersSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty())
                    adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty())
                    adapter.filter(newText);
                return false;
            }

        });

        usersSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter.filter("");
                //Keyboard.hideKeyboard(getContext());
                return false;
            }
        });


        return view;
    }



    public void requestData(final String url, String query, Role role){
        RequestPackage requestPackage = RequestFormer.getUsersRequestPackage(view.getContext(), url, query, role);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());
        client.setResponseTimeout(2000);
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
                Log.i("Status", statusCode+"");
                ResponseErrorHandler.showErrorMessage(view.getContext(), statusCode);

            }

        });
    }

}
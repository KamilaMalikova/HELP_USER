package com.kamilamalikova.help.ui.settings.tabfragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.kamilamalikova.help.LogInActivity;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.FileStream;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.model.SettingsObject;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.settings.adapter.ItemAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class CategorySettingsFragment extends Fragment {
    SessionManager sessionManager;
    ListView categoryListView;
    View view;
    volatile EditText text;
    volatile SwipeRefreshLayout swipeRefreshLayout;

    public CategorySettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_category_settings, container, false);
        sessionManager = new SessionManager(view.getContext());
        categoryListView = view.findViewById(R.id.categoryListView);
        requestData();

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final SettingsObject object = (SettingsObject)categoryListView.getAdapter().getItem(position);
                final View popupView = inflater.inflate(R.layout.fragment_editing_settings, null);
                text = popupView.findViewById(R.id.valueSettingTextEdit);
                text.setText(object.getValue());

                TextView idTextView = popupView.findViewById(R.id.idTextView);
                idTextView.setText(object.getId());
                TextView typeTextView = popupView.findViewById(R.id.typeTextView);
                typeTextView.setText("category");

                Button saveBtn = popupView.findViewById(R.id.saveSettingBtn);
                Button deleteBtn = popupView.findViewById(R.id.saveDeleteBtn);


                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;


                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
                popupWindow.setTouchable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);


                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateData(object.getId(), text.getText().toString(), "category");
                        popupWindow.dismiss();
                    }
                });

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateData(object.getId(), null, "category");
                        popupWindow.dismiss();

                    }
                });

            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View popupView = inflater.inflate(R.layout.fragment_category_dialog, null);
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;

                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
                popupWindow.setTouchable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                final EditText categoryAddEditText = popupView.findViewById(R.id.categoryAddTextEdit);
                final Button cancelBtn = popupView.findViewById(R.id.saveDeleteBtn);
                Button addBtn = popupView.findViewById(R.id.saveSettingBtn);

                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (categoryAddEditText.getText().toString().isEmpty() || categoryAddEditText.getText() == null){
                         Snackbar.make(popupView, "Заполните поле категрии", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        }else {
                            addData(categoryAddEditText.getText().toString());
                            cancelBtn.callOnClick();
                        }
                    }
                });

                // Cancel button
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

            }
        });

        return view;
    }


    private void addData(final String value){
        RequestPackage requestPackage = RequestFormer.getRequestPackageWithKey(view.getContext(), "/categories", "category", value);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());
        final CategorySettingsFragment thisFragment = this;
        client.post(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONObject responseObject = new JSONObject(new String(responseBody));
                    Log.i("Category response", responseObject.toString());
                    thisFragment.requestData();
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

    private void requestData(){
        RequestPackage requestPackage = RequestFormer.getRequestPackage(view.getContext(), "/categories");
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

        client.get(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONArray responseArray = new JSONArray(new String(responseBody));
                    Log.i("Category response", responseArray.toString());
                    ItemAdapter itemAdapter = new ItemAdapter(view.getContext(), responseArray, "category");
                    categoryListView.setAdapter(itemAdapter);
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

    private void updateData(String id, String updateValue, String type){
        String url = "";
        String key = "";
        String value = null;
        if (type.equals("category")) {
            url = "/categories/category/"+id;
            if (updateValue != null){
                key = "category";
                value = updateValue;
            }
        }
        else {
            url = "/units/unit/"+id;
            if (updateValue != null){
                key = "unit";
                value =updateValue;
            }
        }

        RequestPackage requestPackage = RequestFormer.getSettingsRequestPackage(view.getContext(), url, key, value);

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());
        final CategorySettingsFragment thisFragment = this;
        if (updateValue == null){
            client.post(requestPackage.getFullUrl(), new AsyncHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.i("Status", statusCode+"");
                    try {
                        JSONObject responseObject = new JSONObject(new String(responseBody));
                        Log.i("Category response", responseObject.toString());
                        thisFragment.requestData();
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
        }else client.post(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONObject responseObject = new JSONObject(new String(responseBody));
                    Log.i("Category response", responseObject.toString());
                    thisFragment.requestData();

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
package com.kamilamalikova.help.ui.settings.tabfragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

    ListView categoryListView;
    View thisView;

    public CategorySettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_category_settings, container, false);
        thisView = view;

        categoryListView = view.findViewById(R.id.categoryListView);
        requestData();

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SettingsObject object = (SettingsObject)categoryListView.getAdapter().getItem(position);


                final View popupView =
                        //EditingSettingsFragment.newInstance(object.getId(), object.getValue(), "category");
                        inflater.inflate(R.layout.fragment_editing_settings, null);
                EditText text = popupView.findViewById(R.id.valueSettingTextEdit);
                text.setText(object.getValue());

                TextView idTextView = popupView.findViewById(R.id.idTextView);
                idTextView.setText(object.getId());
                TextView typeTextView = popupView.findViewById(R.id.typeTextView);
                typeTextView.setText("category");

                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.MATCH_PARENT;

                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
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
                int height = LinearLayout.LayoutParams.MATCH_PARENT;

                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                final EditText categoryAddEditText = popupView.findViewById(R.id.categoryAddTextEdit);
                final Button cancelBtn = popupView.findViewById(R.id.saveCancelBtn);
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

                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });


            }
        });

        return view;
    }


    private void addData(String value){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl("/categories");
        requestPackage.setParam("category", value);


        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(requestPackage.getJsonObject().toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));


        Log.i("SER", requestPackage.getFullUrl() + entity);
        Log.i("SER", requestPackage.getFullUrl() + requestPackage.getJsonObject());

        LoggedInUser loggedInUser = new FileStream().readUser(getActivity().getDir("data", Context.MODE_PRIVATE));

        if (loggedInUser == null){
            startIntentLogIn();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), loggedInUser.getAuthorizationToken());

        final CategorySettingsFragment thisFragment = this;

        client.post(getContext(), requestPackage.getFullUrl(), entity, "application/json", new AsyncHttpResponseHandler(){
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
                if (statusCode == 403){
                    Snackbar.make(getView(), "Необходимо заново авторизоваться", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    startIntentLogIn();
                    return;
                }else {
                    Snackbar.make(getView(), "Неизвестная ошибка! "+statusCode, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            }
        });
    }

    private void requestData(){

        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.GET);
        requestPackage.setUrl("/categories");


        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(requestPackage.getJsonObject().toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));


        Log.i("SER", requestPackage.getFullUrl() + entity);
        Log.i("SER", requestPackage.getFullUrl() + requestPackage.getJsonObject());

        LoggedInUser loggedInUser = new FileStream().readUser(getActivity().getDir("data", Context.MODE_PRIVATE));

        if (loggedInUser == null){
            startIntentLogIn();
            return;
        }


        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), loggedInUser.getAuthorizationToken());

        client.get(getContext(), requestPackage.getFullUrl(), entity, entity.getContentType().toString(), new AsyncHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONArray responseArray = new JSONArray(new String(responseBody));
                    Log.i("Category response", responseArray.toString());
                    ItemAdapter itemAdapter = new ItemAdapter(getContext(), responseArray, "category");
                    categoryListView.setAdapter(itemAdapter);
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

    private void startIntentLogIn(){
        Intent startIntent = new Intent(getContext(), LogInActivity.class);
        startActivity(startIntent);
    }
}
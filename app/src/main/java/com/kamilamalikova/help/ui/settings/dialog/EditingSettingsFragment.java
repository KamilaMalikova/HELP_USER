package com.kamilamalikova.help.ui.settings.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.kamilamalikova.help.LogInActivity;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.FileStream;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class EditingSettingsFragment extends Fragment {
    Context context;
    SessionManager sessionManager;
    private String id;
    private String value;
    private String type;

    public EditingSettingsFragment() {
    }

    public EditingSettingsFragment(String id, String value, String type) {
        this.id = id;
        this.value = value;
        this.type = type;
    }

    public String getValueId() {
        return id;
    }

    public void setValueId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_editing_settings, container, false);
        this.context = view.getContext();
        sessionManager = new SessionManager(context);
        EditText valueEditText = view.findViewById(R.id.valueSettingTextEdit);
        TextView idTextView = view.findViewById(R.id.idUnitTextView);
        TextView typeTextView = view.findViewById(R.id.typeUnitTextView);

        setValueId(idTextView.getText().toString());
        setValue(valueEditText.getText().toString());
        setType(typeTextView.getText().toString());

        Log.i("Update", id+" - "+value+" - "+type);

        Button saveBtn = view.findViewById(R.id.saveSettingBtn);
        Button cancelBtn = view.findViewById(R.id.saveDeleteBtn);


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(id, value);
            }
        });

        return view;

    }


    private void updateData(String id, String updateValue){
        final RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.POST);
        if (this.type.equals("category")) {
            requestPackage.setUrl("/categories/category/"+id);
            if (updateValue != null){
                requestPackage.setParam("category", value);
            }
        }
        else {
            requestPackage.setUrl("/units/unit/"+id);
            if (updateValue != null){
                requestPackage.setParam("unit", value);
            }
        }
        requestPackage.getBytes();
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

        client.post(getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONObject responseObject = new JSONObject(new String(responseBody));
                    Log.i(type+" response", responseObject.toString());
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


    private void startIntentLogIn(){
        Intent startIntent = new Intent(getContext(), LogInActivity.class);
        startActivity(startIntent);
    }
}
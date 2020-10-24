package com.kamilamalikova.help.ui.settings.tabfragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.EatingPlace;
import com.kamilamalikova.help.model.FileStream;
import com.kamilamalikova.help.model.Keyboard;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.products.fragments.AddProductFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class TablesSettingsFragment extends Fragment {
    SessionManager sessionManager;
    View view;
    EditText tablesCountEditText;
    Button addTablesBtn;
    Button deleteTableBtn;
    TextView tablesCount;
    public TablesSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tables_settings, container, false);
        sessionManager = new SessionManager(view.getContext());
        tablesCountEditText = view.findViewById(R.id.tablesCountEditText);
        addTablesBtn = view.findViewById(R.id.addTablesBtn);
        deleteTableBtn = view.findViewById(R.id.deleteTableBtn);
        tablesCount = view.findViewById(R.id.tablesCount);

        addTablesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(tablesCountEditText.getText().toString()) <= 0)
                {
                    Toast.makeText(view.getContext(), "Минимальное число столов 1", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                tableEdit(URLs.POST_TABLES.getName(), Integer.parseInt(tablesCountEditText.getText().toString()));
                Keyboard.hideKeyboard(view.getContext());
            }
        });

        deleteTableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(tablesCountEditText.getText().toString()) <= 0)
                {
                    Toast.makeText(view.getContext(), "Минимальное число столов 1", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                tableEdit(URLs.POST_TABLES_DELETE.getName(), Integer.parseInt(tablesCountEditText.getText().toString()));
                Keyboard.hideKeyboard(view.getContext());
            }
        });
        return view;
    }


    private void tableEdit(String url, final int count){
        RequestPackage requestPackage = RequestFormer.getRequestPackageWithKey(view.getContext(), url, "count", count+"");
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());


        client.post(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONObject object = new JSONObject(new String(responseBody));
                    Log.i("Response",object.toString());
                    Toast.makeText(view.getContext(), "Операция выполнена успешно!", Toast.LENGTH_LONG)
                            .show();
                    tablesCount.setText((getString(R.string.tables)+": "+object.getInt("table_id")));
                } catch (Exception e) {
                    Integer counter = Integer.valueOf(new String(responseBody));
                    Log.i("Response",counter.toString());
                    Toast.makeText(view.getContext(), "Операция выполнена успешно!", Toast.LENGTH_LONG)
                            .show();
                    tablesCount.setText((getString(R.string.tables)+": "+counter.toString()));
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
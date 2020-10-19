package com.kamilamalikova.help.ui.settings.tabfragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.Tip;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class TipFragment extends Fragment {
    View view;
    Tip tip;
    EditText tipEditText;
    Button saveTipButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tip, container, false);
        tipEditText = view.findViewById(R.id.tipEditText);
        saveTipButton = view.findViewById(R.id.saveTipBtn);

        saveTipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double tip_val = Double.parseDouble(tipEditText.getText().toString());
                if (tip_val < 0.0) {
                    Toast.makeText(getContext(), "Чаевые не могут быть отрицательными", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                saveTip(URLs.POST_TIP.getName(), tip_val);

            }
        });
        getTip(URLs.GET_TIP.getName());
        return view;
    }

    private void getTip(String url){
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
        assert loggedInUser != null;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), loggedInUser.getAuthorizationToken());

        client.get(getContext(), requestPackage.getFullUrl(), entity, "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONObject object = new JSONObject(new String(responseBody));
                    Log.i("Response",object.toString());
                    tip = new Tip(object);
                    tipEditText.setText((tip.getTip()+""));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Status", statusCode+"");
                if (statusCode == 403){
                    Snackbar.make(getView(), "Необходимо заново авторизоваться", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }else {
                    Snackbar.make(getView(), "Неизвестная ошибка! "+statusCode, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    private void saveTip(String url, double tip_val){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url);
        requestPackage.setParam("tip", tip_val+"");
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
        assert loggedInUser != null;

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), loggedInUser.getAuthorizationToken());

        client.post(getContext(), requestPackage.getFullUrl(), entity, "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONObject object = new JSONObject(new String(responseBody));
                    Log.i("Response",object.toString());
                    Toast.makeText(getContext(), "Операция выполнена успешно!", Toast.LENGTH_LONG)
                            .show();
                    tip = new Tip(object);
                    tipEditText.setText((tip.getTip()+""));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Status", statusCode+"");
                if (statusCode == 403){
                    Snackbar.make(getView(), "Необходимо заново авторизоваться", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }else {
                    Snackbar.make(getView(), "Неизвестная ошибка! "+statusCode, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }
}
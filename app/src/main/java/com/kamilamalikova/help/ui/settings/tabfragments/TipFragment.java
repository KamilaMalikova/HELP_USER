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
import com.kamilamalikova.help.model.Keyboard;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.SessionManager;
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
    SessionManager sessionManager;
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
        sessionManager = new SessionManager(view.getContext());
        tipEditText = view.findViewById(R.id.tipEditText);
        saveTipButton = view.findViewById(R.id.saveTipBtn);

        saveTipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double tip_val = (tipEditText.getText().toString().isEmpty()) ? tip.getTip():Double.parseDouble(tipEditText.getText().toString());
                if (tip_val < 0.0) {
                    Toast.makeText(view.getContext(), "Чаевые не могут быть отрицательными", Toast.LENGTH_SHORT)
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
        RequestPackage requestPackage = RequestFormer.getRequestPackage(view.getContext(), url);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());
        client.get(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler(){
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
                ResponseErrorHandler.showErrorMessage(view.getContext(), statusCode);
            }
        });
    }

    private void saveTip(String url, double tip_val){
        RequestPackage requestPackage = RequestFormer.getRequestPackageWithKey(view.getContext(), url, "tip", tip_val+"");
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());

        client.post(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Status", statusCode+"");
                try {
                    JSONObject object = new JSONObject(new String(responseBody));
                    Log.i("Response",object.toString());
                    Toast.makeText(view.getContext(), "Операция выполнена успешно!", Toast.LENGTH_SHORT)
                            .show();
                    tip = new Tip(object);
                    tipEditText.setText((tip.getTip()+""));
                    Keyboard.hideKeyboard(view.getContext());
                } catch (Exception e) {
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
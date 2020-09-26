package com.kamilamalikova.help;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kamilamalikova.help.jwt.Jwt;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.request.ResponsePackage;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Node;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;
import io.jsonwebtoken.Claims;

public class LogInActivity extends AppCompatActivity {

    EditText usernameTextEdit;
    EditText passwordTextEdit;
    Button logInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        usernameTextEdit = (EditText)findViewById(R.id.usernameTextEdit);
        passwordTextEdit = (EditText)findViewById(R.id.passwordTextEdit);
        logInBtn = (Button)findViewById(R.id.loginBtn);

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final RequestPackage requestPackage = new RequestPackage();
                requestPackage.setMethod(RequestType.POST);
                requestPackage.setUrl("/login");
                requestPackage.setParam("username", usernameTextEdit.getText().toString());
                requestPackage.setParam("password", passwordTextEdit.getText().toString());

                ByteArrayEntity entity = null;
                try {
                    entity = new ByteArrayEntity(requestPackage.getJsonObject().toString().getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                Log.i("SER", requestPackage.getFullUrl() + entity);
                Log.i("SER", requestPackage.getFullUrl() + requestPackage.getJsonObject());

                AsyncHttpClient client = new AsyncHttpClient();
                client.post(getApplicationContext(),requestPackage.getFullUrl(), entity, "application/json", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.i("Success", statusCode+"");
                        for (int i = 0; i < headers.length; i++) {
                            if (headers[i].getName().equals("Authorization")){
                                try {

                                    Claims claims = Jwt.decodeJWT(headers[i].getValue());

                                    ArrayList<LinkedHashMap<String, String>> roles = (ArrayList<LinkedHashMap<String, String>>) claims.get("authorities");

                                    String role = "";
                                    for (LinkedHashMap<String, String> sub_role: roles) {
                                        if (sub_role.get("authority").contains("ROLE_")){
                                            role = sub_role.get("authority");
                                            break;
                                        }
                                    }

                                    Log.i("Role", role);

                                    LoggedInUser loggedInUser = new LoggedInUser("", claims.getSubject(), role);

                                    Intent intent = new Intent(getApplicationContext(), TerminalActivity.class);
                                    intent.putExtra("user", loggedInUser);

                                    startActivity(intent);
                                    return;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return;
                                }

                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.i("Failure", statusCode+"");
                        Toast.makeText(getApplicationContext(), statusCode+" - "+error.getMessage(), Toast.LENGTH_LONG).show();
                        error.getStackTrace();
                    }
                });
            }
        });
    }



}
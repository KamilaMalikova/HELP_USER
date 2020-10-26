package com.kamilamalikova.help;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        usernameTextEdit = (EditText)findViewById(R.id.usernameTextEdit);
        passwordTextEdit = (EditText)findViewById(R.id.passwordTextEdit);
        logInBtn = (Button)findViewById(R.id.loginBtn);

        sessionManager = new SessionManager(getApplicationContext());

        if (sessionManager.getIp().equals("")){
            Intent intent = new Intent(getApplicationContext(), IpActivity.class);
            startActivity(intent);
            finish();
        }

        logInBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final String username = usernameTextEdit.getText().toString().trim();
               final String password = passwordTextEdit.getText().toString().trim();
               if (password.equals("")){
                    passwordTextEdit.setError("Введите пароль");
               }
                   AsyncHttpClient client = new AsyncHttpClient();
                   RequestPackage requestPackage = getRequestPackage();

                   client.post(getApplicationContext(), requestPackage.getFullUrl(), requestPackage.getBytes(), "application/json", new AsyncHttpResponseHandler() {
                       @Override
                       public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                           String token = getAuthorizationToken(headers);
                           if (token.equals("")) {
                               Toast.makeText(getApplicationContext(), R.string.autorization_error_wrong_password, Toast.LENGTH_SHORT)
                                       .show();
                               return;
                           }else {
                               //store user data in session manager
                               sessionManager.setLogin(true);
                               sessionManager.setUserName(username);
                               sessionManager.setPassword(password);
                               sessionManager.setAuthorizationToken(token);
                               String role = getRoleFromToken(token);
                               if(!role.equals("")) sessionManager.setRole(role);


                               LoggedInUser loggedInUser = new LoggedInUser("", username, role, token);

                               //Redirect to navigation activity
                               Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                               intent.putExtra("com.kamilamalikova.help.user", (Parcelable) loggedInUser);
                               startActivity(intent);
                               finish();
                           }

                       }

                       @Override
                       public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                           Log.i("Failure", statusCode+"");
                           switch (statusCode){
                               case 403 :
                                   Toast.makeText(getApplicationContext(), R.string.autorization_error_wrong_password, Toast.LENGTH_SHORT)
                                           .show();
                                   break;
                               default: Toast.makeText(getApplicationContext(), statusCode+" - "+error.getMessage(), Toast.LENGTH_SHORT)
                                       .show();
                                   break;
                           }

                           error.getStackTrace();
                       }
                   });
           }
        });

        if (sessionManager.getLogin()){

            //have to delete and remove
            LoggedInUser loggedInUser = new LoggedInUser("", sessionManager.getUsername(), sessionManager.getRole(), sessionManager.getAuthorizationToken());
            //Redirect to navigation activity
            Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
            intent.putExtra("com.kamilamalikova.help.user", (Parcelable) loggedInUser);
            startActivity(intent);
            finish();
        }
    }

    public RequestPackage getRequestPackage(){
        RequestPackage requestPackage = new RequestPackage(getApplicationContext());
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl("/login");
        requestPackage.setParam("username", usernameTextEdit.getText().toString());
        requestPackage.setParam("password", passwordTextEdit.getText().toString());

        return requestPackage;
    }

    public String getAuthorizationToken(Header[] headers){
        for (int i = 0; i< headers.length; i++){
            if (headers[i].getName().equals(getString(R.string.authorizationToken))) return headers[i].getValue();
        }
        return "";
    }

    public String getRoleFromToken(String token){
        Claims claims = Jwt.decodeJWT(token);

        ArrayList<LinkedHashMap<String, String>> roles = (ArrayList<LinkedHashMap<String, String>>) claims.get("authorities");

        String role = "";
        for (LinkedHashMap<String, String> sub_role: roles) {
            if (sub_role.get("authority").contains("ROLE_")){
                role = sub_role.get("authority");
                break;
            }
        }
        return role;
    }

}
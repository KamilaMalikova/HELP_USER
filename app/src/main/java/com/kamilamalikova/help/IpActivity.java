package com.kamilamalikova.help;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kamilamalikova.help.model.Keyboard;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.request.AsyncRequest;
import com.kamilamalikova.help.request.AsyncResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class IpActivity extends AppCompatActivity implements AsyncResponse {
    SessionManager sessionManager;
    AsyncRequest asyncRequest;
    AsyncResponse thisResponse;
    String ip;
    boolean reached = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip);
        thisResponse = this;
        sessionManager = new SessionManager(getApplicationContext());
        final EditText ipEditText = findViewById(R.id.ipTextNumber);
        asyncRequest = new AsyncRequest();
        asyncRequest.delegate = this;
        Button saveIp = findViewById(R.id.saveIpBtn);

        saveIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asyncRequest == null){
                    asyncRequest = new AsyncRequest();
                    asyncRequest.delegate = thisResponse;
                }
                if (!reached){
                    Toast.makeText(getApplicationContext(), "Проверка...", Toast.LENGTH_SHORT)
                            .show();
                }
                ip = ipEditText.getText().toString();
                asyncRequest.server = ip;
                asyncRequest.execute();

            }
        });
    }

    @Override
    public void processFinish(boolean reachable) {
        reached = true;
        if (reachable){
            sessionManager.setIp(ip);
            Keyboard.hideKeyboard(getApplicationContext());
            Toast.makeText(getApplicationContext(), getString(R.string.saved), Toast.LENGTH_SHORT)
                    .show();
            Intent startIntent = new Intent(getApplicationContext(), LogInActivity.class);
            startActivity(startIntent);
        }else {
            Toast.makeText(getApplicationContext(), getString(R.string.wrong_addres), Toast.LENGTH_SHORT)
                    .show();
        }
        asyncRequest = null;
    }
}
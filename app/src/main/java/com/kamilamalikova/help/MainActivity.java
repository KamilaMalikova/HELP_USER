package com.kamilamalikova.help;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.kamilamalikova.help.jwt.Jwt;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.SessionManager;

import org.threeten.bp.Instant;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.TimeZone;

import io.jsonwebtoken.Claims;

public class MainActivity extends AppCompatActivity {
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getApplicationContext());

        setContentView(R.layout.activity_main);

        if (sessionManager.getLogin()){
            //have to delete and remove
            LoggedInUser loggedInUser = new LoggedInUser("", sessionManager.getUsername(), sessionManager.getRole(), sessionManager.getAuthorizationToken());

            //Redirect to navigation activity
            Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
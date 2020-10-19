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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            File file_ip = getDir("data", Context.MODE_PRIVATE);
            File serFile_ip = new File(file_ip.getAbsoluteFile()+"/ip.ser");
            Log.i("File", serFile_ip.getAbsolutePath());
            if (!serFile_ip.exists()){
                Intent startIntent = new Intent(getApplicationContext(), IpActivity.class);
                startActivity(startIntent);
            }else {
                File file = getDir("data", Context.MODE_PRIVATE);
                File serFile = new File(file.getAbsoluteFile() + "/user.ser");
                Log.i("File", serFile.getAbsolutePath());
                FileInputStream streamIn = new FileInputStream(serFile);
                ObjectInputStream objectInputStream = new ObjectInputStream(streamIn);
                LoggedInUser loggedInUser = (LoggedInUser) objectInputStream.readObject();
                if (loggedInUser == null) {
                    startIntentLogIn();
                } else {
                    Claims claims = Jwt.decodeJWT(loggedInUser.getAuthorizationToken());
                    long expiration = claims.get("exp", Long.class);
                    Instant expiration_intent = Instant.ofEpochSecond(expiration);
                    Instant now = Instant.now();
                    if (now.compareTo(expiration_intent) >= 0) {
                        startIntentLogIn();
                    } else {
                        startIntentNavigation(loggedInUser);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            startIntentLogIn();
            e.printStackTrace();
        } catch (IOException e) {
            startIntentLogIn();
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            startIntentLogIn();
            e.printStackTrace();
        }
    }

    private void startIntentLogIn(){
        Intent startIntent = new Intent(getApplicationContext(), LogInActivity.class);
        startActivity(startIntent);
    }

    private void startIntentNavigation(LoggedInUser loggedInUser){
        Intent startIntent = new Intent(getApplicationContext(), NavigationActivity.class);
        startIntent.putExtra("com.kamilamalikova.help.user", (Parcelable) loggedInUser);
        startActivity(startIntent);
    }

}
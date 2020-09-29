package com.kamilamalikova.help;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;

import com.kamilamalikova.help.model.LoggedInUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginScreenBtn = (Button)findViewById(R.id.loginActivityBtn);
        loginScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File file = getDir("data", Context.MODE_PRIVATE);
                    File serFile = new File(file.getAbsoluteFile()+"/user.ser");
                    FileInputStream streamIn = new FileInputStream(serFile);
                    ObjectInputStream objectInputStream = new ObjectInputStream(streamIn);
                    LoggedInUser loggedInUser = (LoggedInUser) objectInputStream.readObject();
                    if (loggedInUser == null){
                        startIntentLogIn();
                    }else {
                        startIntentNavigation(loggedInUser);
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
        });
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
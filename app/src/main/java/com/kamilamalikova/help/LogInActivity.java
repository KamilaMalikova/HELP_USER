package com.kamilamalikova.help;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

            }
        });
    }
}
package com.kamilamalikova.help;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kamilamalikova.help.model.LoggedInUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class IpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip);

        final EditText ipEditText = findViewById(R.id.ipTextNumber);
        Button saveIp = findViewById(R.id.saveIpBtn);

        saveIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipEditText.getText().toString();
                saveSerializable(ip);
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startIntent);
            }
        });
    }

    private void saveSerializable(String ip){
        try {
            File file = getDir("data", Context.MODE_PRIVATE);
            File serFile = new File(file.getAbsoluteFile()+"/ip.ser");
            FileOutputStream fileOutputStream = new FileOutputStream(serFile, false);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(ip);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
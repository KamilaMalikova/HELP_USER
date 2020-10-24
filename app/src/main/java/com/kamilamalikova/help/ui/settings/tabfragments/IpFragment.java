package com.kamilamalikova.help.ui.settings.tabfragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kamilamalikova.help.MainActivity;
import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.Keyboard;
import com.kamilamalikova.help.model.SessionManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class IpFragment extends Fragment {
    SessionManager sessionManager;
    View view;
    EditText ipEditText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ip, container, false);
        sessionManager = new SessionManager(view.getContext());
        ipEditText = view.findViewById(R.id.ipEditText);
        Button saveIp = view.findViewById(R.id.saveIpBtn);
        String server = sessionManager.getIp();
        this.ipEditText.setText(server);

        saveIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipEditText.getText().toString();
                sessionManager.setIp(ip);
                Keyboard.hideKeyboard(view.getContext());
                Toast.makeText(view.getContext(), getString(R.string.saved), Toast.LENGTH_LONG)
                        .show();
            }
        });
        return view;
    }
}
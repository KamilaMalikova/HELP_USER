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
import com.kamilamalikova.help.request.AsyncRequest;
import com.kamilamalikova.help.request.AsyncResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;


public class IpFragment extends Fragment  implements AsyncResponse {
    SessionManager sessionManager;
    View view;
    EditText ipEditText;
    String ip;
    IpFragment thisFragment;
    AsyncRequest asyncRequest;
    boolean reached = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        thisFragment = this;
        view = inflater.inflate(R.layout.fragment_ip, container, false);
        sessionManager = new SessionManager(view.getContext());
        ipEditText = view.findViewById(R.id.ipEditText);
        Button saveIp = view.findViewById(R.id.saveIpBtn);
        String server = sessionManager.getIp();
        this.ipEditText.setText(server);
        asyncRequest = new AsyncRequest();
        asyncRequest.delegate = this;

        saveIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asyncRequest == null){
                    asyncRequest = new AsyncRequest();
                    asyncRequest.delegate = thisFragment;
                }
                ip = ipEditText.getText().toString();
                if (!reached) Toast.makeText(view.getContext(), "Проверка...", Toast.LENGTH_SHORT)
                                    .show();
                asyncRequest.server = ip;
                asyncRequest.execute();
            }
        });
        return view;
    }

    @Override
    public void processFinish(boolean reachable) {
        reached = true;
        if (reachable){
            sessionManager.setIp(ip);
            Keyboard.hideKeyboard(view.getContext());
            Toast.makeText(view.getContext(), getString(R.string.saved), Toast.LENGTH_SHORT)
                    .show();
        }else {
            Toast.makeText(view.getContext(), getString(R.string.wrong_addres), Toast.LENGTH_SHORT)
                    .show();
        }
        asyncRequest = null;
    }
}
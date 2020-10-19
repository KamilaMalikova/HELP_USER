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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class IpFragment extends Fragment {
    View view;
    EditText ipEditText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_ip, container, false);
        ipEditText = view.findViewById(R.id.ipEditText);
        Button saveIp = view.findViewById(R.id.saveIpBtn);



        try {
            @SuppressLint("SdCardPath") File file = new File("/data/user/0/com.kamilamalikova.help/app_data/");
            File serFile = new File(file.getAbsoluteFile()+"/ip.ser");
            Log.i("File", serFile.getAbsolutePath());
            FileInputStream streamIn = new FileInputStream(serFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(streamIn);
            String server = (String)objectInputStream.readObject();
            this.ipEditText.setText(server);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        saveIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipEditText.getText().toString();
                saveSerializable(ip);
                Keyboard.hideKeyboard(getContext());
                Toast.makeText(getContext(), getString(R.string.saved), Toast.LENGTH_LONG)
                        .show();
            }
        });
        return view;
    }


    private void saveSerializable(String ip){
        try {
            File file = getActivity().getDir("data", Context.MODE_PRIVATE);
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
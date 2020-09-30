package com.kamilamalikova.help.model;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;

public class FileStream extends AppCompatActivity {

    public LoggedInUser readUser(File file){
        try {
            File serFile = new File(file.getAbsoluteFile()+"/user.ser");
            FileInputStream streamIn = null;
            streamIn = new FileInputStream(serFile);
            ObjectInputStream objectInputStream = new ObjectInputStream(streamIn);
            LoggedInUser loggedInUser = (LoggedInUser) objectInputStream.readObject();
            return loggedInUser;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}

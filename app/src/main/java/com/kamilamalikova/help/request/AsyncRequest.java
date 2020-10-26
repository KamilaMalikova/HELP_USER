package com.kamilamalikova.help.request;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.Socket;

public class AsyncRequest extends AsyncTask<String, String, Boolean> {

    public AsyncResponse delegate = null;
    public String server;

    public boolean isReachable(String server, int timeout){
        final String prefix = "http://";
        final String port = "8080";
        return  isHostReachable(server, 8080, timeout);
    }

    public static boolean isHostReachable(String serverAddress, int serverTCPport, int timeoutMS){
        try (Socket s = new Socket(serverAddress, serverTCPport)) {
            return true;
        } catch (IOException ex) {
            /* ignore */
        }
        return false;
    }


    @Override
    protected Boolean doInBackground(String... strings) {
        return isReachable(server, 200);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        delegate.processFinish(aBoolean);
    }
}

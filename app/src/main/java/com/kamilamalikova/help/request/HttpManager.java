package com.kamilamalikova.help.request;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

public class HttpManager {

    public void post(final RequestPackage requestPackage,
                     final Context context, ResponsePackage responsePackage) throws UnsupportedEncodingException, InterruptedException {

        ByteArrayEntity entity = new ByteArrayEntity(requestPackage.getJsonObject().toString().getBytes("UTF-8"));
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        Log.i("SER", requestPackage.getFullUrl() + entity);
        Log.i("SER", requestPackage.getFullUrl() + requestPackage.getJsonObject());

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(context, requestPackage.getFullUrl(), entity, "application/json", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i("Status", statusCode+"");
                try {
                    Log.i("SER", response.toString());
                    for (int i = 0; i < headers.length; i++) {
                        System.out.println(headers[i]);
                    }
                    responsePackage.jsonObjectResult = response;

                } catch (Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i("Status", statusCode+"");
                try {
                    Log.i("SER", responseString);
                    ResponsePackage.stringResponse = responseString;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.i("Status", statusCode+"");
                if (statusCode == 404) {
                    Toast.makeText(context, "404 - Not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(context, "500 - !", Toast.LENGTH_LONG).show();
                } else if (statusCode == 403) {
                    Toast.makeText(context, "403 - Forbidden", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, throwable.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("Status", statusCode+"");
                if (statusCode == 200){
                    for (int i = 0; i < headers.length; i++) {
                        if (headers[i].getName().equals("Authorization")) {
                            responsePackage.set = headers[i].getValue();
                            Log.i("Headers",ResponsePackage.authorizationToken);
                            return;
                        }
                    }
                    ResponsePackage.authorizationToken = "notfound";
                }else if (statusCode == 404) {
                    Toast.makeText(context, "404 - Not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(context, "500 - !", Toast.LENGTH_LONG).show();
                } else if (statusCode == 403) {
                    Toast.makeText(context, "403 - Forbidden", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, throwable.toString(), Toast.LENGTH_LONG).show();
                };
            }
        });

        client.getThreadPool().awaitTermination(3000, TimeUnit.MILLISECONDS);
    }
}

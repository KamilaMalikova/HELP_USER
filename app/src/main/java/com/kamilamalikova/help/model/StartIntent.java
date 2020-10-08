package com.kamilamalikova.help.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.kamilamalikova.help.LogInActivity;

public class StartIntent {
    public static void startIntentLogIn(Context context, Activity activity){
        Intent startIntent = new Intent(context, LogInActivity.class);
        activity.startActivity(startIntent);
    }
}

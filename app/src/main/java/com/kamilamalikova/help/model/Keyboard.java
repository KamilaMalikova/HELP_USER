package com.kamilamalikova.help.model;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Keyboard {

    public static void hideKeyboard( Context context ) {

        try {
            InputMethodManager inputManager = ( InputMethodManager ) context.getSystemService( Context.INPUT_METHOD_SERVICE );

            View view = ( (Activity) context ).getCurrentFocus();
            if ( view != null ) {
                inputManager.hideSoftInputFromWindow( view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}

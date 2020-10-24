package com.kamilamalikova.help.model;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SessionManager(Context context){
        sharedPreferences = context.getSharedPreferences("AppKey", 0);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public void setLogin(boolean login){
        editor.putBoolean("KEY_LOGIN", login);
        editor.commit();
    }

    public boolean getLogin(){
        return sharedPreferences.getBoolean("KEY_LOGIN", false);
    }

    public void setUserName(String username){
        editor.putString("KEY_USERNAME", username);
        editor.commit();
    }

    public String getUsername(){
        return sharedPreferences.getString("KEY_USERNAME", "");
    }

    public void setPassword(String password){
        editor.putString("KEY_PASSWORD", password);
        editor.commit();
    }

    public String getPassword(String password){
        return sharedPreferences.getString("KEY_PASSWORD", "");
    }

    public void setAuthorizationToken(String token){
        editor.putString("KEY_TOKEN", token);
        editor.commit();
    }

    public String getAuthorizationToken(){
        return sharedPreferences.getString("KEY_TOKEN", "");
    }

    public void setRole(String role){
        editor.putString("KEY_ROLE", role);
        editor.commit();
    }

    public String getRole(){
        return sharedPreferences.getString("KEY_ROLE", "NOTWORKING");
    }

    public void setName(String name){
        commit("KEY_NAME", name);
    }
    public String getName() {
        return sharedPreferences.getString("KEY_NAME", "");
    }

    public void setLastName(String lastname){
        commit("KEY_LASTNAME", lastname);
    }

    public String getLastName() {
        return sharedPreferences.getString("KEY_LASTNAME", "");
    }

    public void setIp(String value){
        commit("KEY_IP", value);
    }
    public String getIp(){
        return sharedPreferences.getString("KEY_IP", "192.168.25.107");
    }
    private void commit(String key, String value){
        editor.putString(key, value);
        editor.commit();
    }


}

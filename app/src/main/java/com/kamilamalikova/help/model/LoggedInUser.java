package com.kamilamalikova.help.model;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.appcompat.app.ActionBarDrawerToggle;

import java.io.Serializable;

public class LoggedInUser implements Parcelable, Serializable{

    private String displayName;
    private String username;
    private UserRole role;
    private String roleString;
    private String authorizationToken;

    public LoggedInUser(String displayName, String username, String role) {
         this.displayName = displayName;
        this.username = username;
        this.setRole(role);
        this.roleString = this.getRole().name();
    }

    public LoggedInUser(String displayName, String username, String roleString, String authorizationToken) {
        this.displayName = displayName;
        this.username = username;
        this.setRole(role);
        this.roleString = roleString;
        this.authorizationToken = authorizationToken;
    }

    public LoggedInUser(SessionManager manager){
        this.displayName = manager.getName()+" "+manager.getLastName();
        this.username = manager.getUsername();
        this.setRole(manager.getRole());
        this.roleString = manager.getRole();
        this.authorizationToken = manager.getAuthorizationToken();
    }
    protected LoggedInUser(Parcel in) {
        displayName = in.readString();
        username = in.readString();
        this.setRole(in.readString());
    }

    public static final Creator<LoggedInUser> CREATOR = new Creator<LoggedInUser>() {
        @Override
        public LoggedInUser createFromParcel(Parcel in) {
            return new LoggedInUser(in);
        }

        @Override
        public LoggedInUser[] newArray(int size) {
            return new LoggedInUser[size];
        }
    };



    public void setRole(String role) {
        switch (role){
            case "ROLE_ADMIN":
            case "ADMIN":
                this.role = UserRole.ADMIN;
                break;
            case "ROLE_OWNER":
            case "OWNER":
                this.role = UserRole.OWNER;
                break;
            case "ROLE_STUFF":
            case "STUFF":
                this.role = UserRole.STUFF;
                break;
            case "ROLE_WAITER":
            case "WAITER":
                this.role = UserRole.WAITER;
                break;
            default: this.role = UserRole.NOTWORKING;
        }
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUsername() {
        return username;
    }

    public UserRole getRole() {
        return role;
    }

    public String getRoleString() {
        return roleString;
    }

    public void setRoleString(String roleString) {
        this.roleString = roleString;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(displayName);
        dest.writeString(username);
        dest.writeString(roleString);
    }


    public static LoggedInUser isLoggedIn(Context context, Activity activity){
        LoggedInUser loggedInUser = new FileStream().readUser(activity.getDir("data", Context.MODE_PRIVATE));

        if (loggedInUser == null){
            StartIntent.startIntentLogIn(context, activity);
            return null;
        }
        else return loggedInUser;
    }

    public boolean isOwnerOrAdmin(){
        return (this.getRole() == UserRole.ADMIN || this.getRole() == UserRole.OWNER);
    }
}

package com.kamilamalikova.help;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.Role;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.model.User;
import com.kamilamalikova.help.model.UserRole;
import com.kamilamalikova.help.request.RequestPackage;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;

public class NavigationActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    String name = "";
    TextView usernameTextView;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getApplicationContext());
        String username = sessionManager.getName();
        String role = role(sessionManager.getRole());

        LoggedInUser loggedInUser = (LoggedInUser) getIntent().getExtras().get("com.kamilamalikova.help.user");
        if(loggedInUser.getRole() == UserRole.STUFF){
            setContentView(R.layout.activity_navigation_stuff);
        }else if (loggedInUser.getRole() == UserRole.WAITER) setContentView(R.layout.activity_navigation_waiter);
        else if (loggedInUser.getRole() == UserRole.NOTWORKING) setContentView(R.layout.activity_navigation_notworking);
        else setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        usernameTextView = headerView.findViewById(R.id.usernameTextView);
        TextView userRoleTextView = headerView.findViewById(R.id.userRoleTextView);

        usernameTextView.setText(username);
        userRoleTextView.setText(role);

        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.logOutBtn);
        MenuItem item = menu.findItem(R.id.logOutBtn);

        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
                builder.setTitle("Вы уверены, что хотите выйти?")
                        .setCancelable(true)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sessionManager.setLogin(false);
                                sessionManager.setUserName("");
                                sessionManager.setAuthorizationToken("");
                                sessionManager.setRole("");
                                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(startIntent);
                                finish();
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return false;
            }
        });

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                 R.id.nav_settings, R.id.nav_products, R.id.nav_stock, R.id.nav_terminal, R.id.nav_in_out_stock, R.id.nav_users, R.id.nav_report, R.id.nav_order_history)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        if (sessionManager.getName().equals("") || sessionManager.getLastName().equals("")) requestUser(URLs.GET_USER.getName(), sessionManager.getUsername());
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0){
            fm.popBackStack();
        }else {
            super.onBackPressed();
        }
    }

    private String role(String role){
        String ru_role = "";
        switch (role){
            case "ROLE_ADMIN":
            case "ADMIN":
                ru_role = Role.ADMIN.getRu_name();
                break;
            case "ROLE_OWNER":
            case "OWNER":
                ru_role = Role.OWNER.getRu_name();
                break;
            case "ROLE_STUFF":
            case "STUFF":
                ru_role = Role.STUFF.getRu_name();
                break;
            case "ROLE_WAITER":
            case "WAITER":
                ru_role = Role.WAITER.getRu_name();
                break;
            default: Role.NOTWORKING.getRu_name();
        }
        return ru_role;
    }
    public void requestUser(String url, String username){
        RequestPackage requestPackage = RequestFormer.getRequestPackage(getApplicationContext(), url+"/"+username);
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());
        client.get(getApplicationContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody));
                    Log.i("Res", response.toString());
                    User user = new User(response);
                    sessionManager.setName(user.getName());
                    sessionManager.setLastName(user.getLastname());
                    usernameTextView.setText(sessionManager.getName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Error", statusCode + " " + new String(responseBody));
                ResponseErrorHandler.showErrorMessage(getApplicationContext(), statusCode);

            }
        });
    }
}
package com.kamilamalikova.help.ui.users;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.Category;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.Product;
import com.kamilamalikova.help.model.RequestFormer;
import com.kamilamalikova.help.model.ResponseErrorHandler;
import com.kamilamalikova.help.model.Role;
import com.kamilamalikova.help.model.SessionManager;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.model.Unit;
import com.kamilamalikova.help.model.User;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.products.adapter.ItemAdapter;
import com.kamilamalikova.help.ui.products.adapter.ItemObject;
import com.kamilamalikova.help.ui.users.adapter.RoleAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class UserFragment extends Fragment {
    SessionManager sessionManager;
    User user;
    View view;

    Spinner roleSpinner;
    RoleAdapter adapter;
    EditText nameEditText;
    EditText lastnameEditText;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText repeatPasswordEditText;

    MenuItem editDone;
    MenuItem edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user, container, false);
        sessionManager = new SessionManager(view.getContext());
        roleSpinner = view.findViewById(R.id.userRoleSpinner);
        adapter = new RoleAdapter(view.getContext());
        roleSpinner.setAdapter(adapter);

        nameEditText = view.findViewById(R.id.userNameEditText);
        lastnameEditText = view.findViewById(R.id.userLastNameEditText);
        usernameEditText = view.findViewById(R.id.userUsernameEditText);
        passwordEditText = view.findViewById(R.id.userPasswordEditText);
        repeatPasswordEditText = view.findViewById(R.id.userRepeated_passwordEditText);

        repeatPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(passwordEditText.getText().toString())){
                    repeatPasswordEditText.setTextColor(getResources().getColor(R.color.red));
                }else {
                    repeatPasswordEditText.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });
        enable(false);
        fill(user);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.edit_menu, menu);
        editDone = menu.findItem(R.id.edit_done_menu);
        editDone.setVisible(false);
        edit = menu.findItem(R.id.edit_menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit_menu){
            item.setVisible(false);
            enable(true);
            editDone.setVisible(true);
        } else if (item.getItemId() == R.id.edit_done_menu){
            item.setVisible(false);
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            String name = nameEditText.getText().toString();
            String lastname = lastnameEditText.getText().toString();
            Role role = (Role)roleSpinner.getSelectedItem();

            if (username.isEmpty() || name.isEmpty() || lastname.isEmpty()){
                Toast.makeText(view.getContext(), "Необходимо заполнить все поля!", Toast.LENGTH_SHORT)
                        .show();
                return true;
            }
            User user = new User(username, password, name, lastname, role, "");
            saveUser(URLs.POST_USER.getName()+"/"+user.getUsername(), user);
        }
        else {
            getActivity().onBackPressed();
        }
        return true;
    }


    public void saveUser(String url, final User user) {
        try {
            RequestPackage requestPackage = RequestFormer.getUserRequestPackage(view.getContext(), url, user);
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader(getString(R.string.authorizationToken), sessionManager.getAuthorizationToken());
            client.post(view.getContext(), requestPackage.getFullUrl(), requestPackage.getEntity(), "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        JSONObject response = new JSONObject(new String(responseBody));
                        Log.i("Res", response.toString());
                        User user1 = new User(response);
                        user.setUser(user1);
                        enable(false);
                        fill(user1);
                        edit.setVisible(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.i("Error", statusCode + " " + new String(responseBody));
                    ResponseErrorHandler.showErrorMessage(view.getContext(), statusCode);
                    enable(false);
                    edit.setVisible(true);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fill(User user){
        roleSpinner.setSelection((adapter.getItemPosition(user.getRole())>-1) ? adapter.getItemPosition(user.getRole()) : 0);
        nameEditText.setText(user.getName());
        lastnameEditText.setText(user.getLastname());
        usernameEditText.setText(user.getUsername());
    }

    private void enable(boolean enabled){
        roleSpinner.setEnabled(enabled);
        nameEditText.setEnabled(enabled);
        lastnameEditText.setEnabled(enabled);
        usernameEditText.setEnabled(enabled);
        passwordEditText.setEnabled(enabled);
        repeatPasswordEditText.setEnabled(enabled);

    }
}
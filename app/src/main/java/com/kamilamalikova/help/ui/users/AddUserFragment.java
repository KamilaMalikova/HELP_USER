package com.kamilamalikova.help.ui.users;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kamilamalikova.help.R;
import com.kamilamalikova.help.model.LoggedInUser;
import com.kamilamalikova.help.model.Order;
import com.kamilamalikova.help.model.Product;
import com.kamilamalikova.help.model.Role;
import com.kamilamalikova.help.model.URLs;
import com.kamilamalikova.help.model.User;
import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.users.adapter.RoleAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;


public class AddUserFragment extends Fragment {
    View view;
    Spinner roleSpinner;
    RoleAdapter adapter;
    EditText nameEditText;
    EditText lastnameEditText;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText repeatPasswordEditText;
    Button saveUserBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_user, container, false);
        roleSpinner = view.findViewById(R.id.roleSpinner);
        adapter = new RoleAdapter(getContext());
        roleSpinner.setAdapter(adapter);

        nameEditText = view.findViewById(R.id.nameEditText);
        lastnameEditText = view.findViewById(R.id.lastNameEditText);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        repeatPasswordEditText = view.findViewById(R.id.repeated_passwordEditText);
        saveUserBtn = view.findViewById(R.id.saveUserBtn);

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

        saveUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (!repeatPasswordEditText.getText().toString().equals(password)){
                    Toast.makeText(getContext(), "Пароль не совпадает!", Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                String name = nameEditText.getText().toString();
                String lastname = lastnameEditText.getText().toString();
                Role role = (Role)roleSpinner.getSelectedItem();

                if (username.isEmpty() || password.isEmpty() || name.isEmpty() || lastname.isEmpty()){
                    Toast.makeText(getContext(), "Необходимо заполнить все поля!", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                User user = new User(username, password, name, lastname, role);
                addUser(URLs.POST_USER.getName(), user);
            }
        });

        return view;
    }

    public void addUser(String url, User user){
        final RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url);
        LoggedInUser loggedInUser = LoggedInUser.isLoggedIn(getContext(), getActivity());
        assert loggedInUser != null;
        user.setCreator(loggedInUser.getUsername());
        ByteArrayEntity entity = null;
        try {
            entity = new ByteArrayEntity(user.generateJsonObject().toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        Log.i("SER", requestPackage.getFullUrl() + entity);
        Log.i("SER", requestPackage.getFullUrl() + requestPackage.getJsonObject());


        AsyncHttpClient client = new AsyncHttpClient();

        client.addHeader(getString(R.string.authorizationToken), loggedInUser.getAuthorizationToken());

        client.post(getContext(), requestPackage.getFullUrl(), entity, "application/json", new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody));
                    Log.i("response", response.toString());
                    Navigation.findNavController(view).navigate(R.id.nav_users);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Status", statusCode+"! "+new String(responseBody));
            }
        });
    }

}
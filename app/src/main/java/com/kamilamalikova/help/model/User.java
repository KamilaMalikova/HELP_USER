package com.kamilamalikova.help.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Filterable;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.spec.DESedeKeySpec;

public class User implements Parcelable {
    private int id;

    private String username;

    private String password;

    private String name;

    private String lastname;

    private int active;

    private Role role;

    private String creator;

    private boolean deleted = false;

    public User(String username, String password, String name, String lastname, Role role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.lastname = lastname;
        this.role = role;
        if (role == Role.NOTWORKING){
            this.deleted = true;
        }else this.deleted = false;
    }

    public User(JSONObject object) throws JSONException {
        this.id = object.getInt("id");
        this.username = object.getString("username");
        this.password = null;
        this.name = object.getString("name");
        this.lastname = object.getString("lastname");
        setRole(object.getString("role"));
    }
    public User(LoggedInUser loggedInUser){
        this.id = 0;
        this.username = loggedInUser.getUsername();
        this.password = null;
        this.name = loggedInUser.getDisplayName();
        this.lastname = loggedInUser.getDisplayName();

        switch (loggedInUser.getRole()){
            case OWNER:
                this.role = Role.OWNER;
                break;
            case ADMIN:
                this.role = Role.ADMIN;
                break;
            case WAITER:
                this.role = Role.WAITER;
                break;
            case STUFF:
                this.role = Role.STUFF;
            case NOTWORKING:
                this.role = Role.NOTWORKING;
        }
    }
    protected User(Parcel in) {
        id = in.readInt();
        username = in.readString();
        password = in.readString();
        name = in.readString();
        lastname = in.readString();
        active = in.readInt();
        creator = in.readString();
        deleted = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setRole(String role){
        this.role = Role.valueOf(role);
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public JSONObject generateJsonObject() throws JSONException {

        String json = "{\"role\":\""+role.name()+"\"," +
                "\"username\":\""+this.username+"\"," +
                "\"password\":\""+this.password+"\"," +
                "\"name\":\""+this.name+"\"," +
                "\"lastname\":\""+this.lastname+"\"," +
                "\"creator\":\""+this.creator+"\", " +
                "\"deleted\":\""+(this.deleted ? 1 : 0)+"\"}";
        return new JSONObject(json);
    }

    public void setUser(User user1) {
        this.id = user1.getId();
        this.username = user1.getUsername();
        this.password = null;
        this.name = user1.getName();
        this.lastname = user1.getLastname();
        this.role = user1.getRole();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.username);
        dest.writeString(password);
        dest.writeString(this.name);
        dest.writeString(this.lastname);
        dest.writeValue(this.role);
        dest.writeValue(this.deleted);

    }
}

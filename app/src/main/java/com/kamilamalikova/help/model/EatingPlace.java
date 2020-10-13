package com.kamilamalikova.help.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class EatingPlace implements Parcelable {
    private int id;

    private boolean reserved;

    private String waiterUsername;

    private String waiterName;

    private boolean active;

    public EatingPlace(int id, boolean reserved, String waiterUsername, String waiterName, boolean active) {
        this.id = id;
        this.reserved = reserved;
        this.waiterUsername = waiterUsername;
        this.waiterName = waiterName;
        this.active = active;
    }

    public EatingPlace(JSONObject object) throws JSONException {
        this.id = object.getInt("id");
        this.reserved = object.getBoolean("reserved");
        this.waiterUsername = object.getString("waiterUsername");
        this.waiterName = object.getString("waiterName");
        this.active = object.getBoolean("active");
    }

    protected EatingPlace(Parcel in) {
        id = in.readInt();
        reserved = in.readByte() != 0;
        waiterUsername = in.readString();
        waiterName = in.readString();
        active = in.readByte() != 0;
    }

    public static final Creator<EatingPlace> CREATOR = new Creator<EatingPlace>() {
        @Override
        public EatingPlace createFromParcel(Parcel in) {
            return new EatingPlace(in);
        }

        @Override
        public EatingPlace[] newArray(int size) {
            return new EatingPlace[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public String getWaiterUsername() {
        return waiterUsername;
    }

    public void setWaiterUsername(String waiterUsername) {
        this.waiterUsername = waiterUsername;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public void setWaiterName(String waiterName) {
        this.waiterName = waiterName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeBoolean(reserved);
        dest.writeString(waiterName);
        dest.writeString(waiterName);
        dest.writeBoolean(active);
    }
}

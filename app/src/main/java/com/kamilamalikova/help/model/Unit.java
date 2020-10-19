package com.kamilamalikova.help.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Unit implements Parcelable {
    private int id;

    private String unitName;

    public Unit(int id, String unitName) {
        this.id = id;
        this.unitName = unitName;
    }

    public Unit(JSONObject unit) throws JSONException {
        this.id = unit.getInt("id");
        this.unitName = unit.getString("unitName");
    }

    protected Unit(Parcel in) {
        id = in.readInt();
        unitName = in.readString();
    }

    public static final Creator<Unit> CREATOR = new Creator<Unit>() {
        @Override
        public Unit createFromParcel(Parcel in) {
            return new Unit(in);
        }

        @Override
        public Unit[] newArray(int size) {
            return new Unit[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.unitName);
    }
}

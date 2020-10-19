package com.kamilamalikova.help.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Category implements Parcelable {
    private int id;

    private String category;

    public Category(int id, String category) {
        this.id = id;
        this.category = category;
    }

    public Category(JSONObject category) throws JSONException {
        this.id = category.getInt("id");
        this.category = category.getString("category");
    }

    protected Category(Parcel in) {
        id = in.readInt();
        category = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.category);
    }
}

package com.kamilamalikova.help.ui.stock.adapter;

import android.os.Parcel;
import android.os.Parcelable;

public class DocTypeObject implements Parcelable {
    String id;
    String value;

    public DocTypeObject(String id, String value) {
        this.id = id;
        this.value = value;
    }

    protected DocTypeObject(Parcel in) {
        this.id = in.readString();
        this.value = in.readString();
    }

    public static final Creator<DocTypeObject> CREATOR = new Creator<DocTypeObject>() {
        @Override
        public DocTypeObject createFromParcel(Parcel in) {
            return new DocTypeObject(in);
        }

        @Override
        public DocTypeObject[] newArray(int size) {
            return new DocTypeObject[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(getId());
        dest.writeValue(getValue());
    }
}

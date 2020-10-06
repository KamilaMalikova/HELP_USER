package com.kamilamalikova.help.ui.stock.adapter;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductItemObject implements Parcelable {
    boolean isChosen;
    String productName;
    Double qty;
    String id;

    public ProductItemObject(boolean isChosen, String id, String productName, Double qty) {
        this.isChosen = isChosen;
        this.productName = productName;
        this.qty = qty;
        this.id = id;
    }

    protected ProductItemObject(Parcel in) {
        isChosen = in.readByte() != 0;
        productName = in.readString();
        if (in.readByte() == 0) {
            qty = null;
        } else {
            qty = in.readDouble();
        }
        id = in.readString();
    }

    public static final Creator<ProductItemObject> CREATOR = new Creator<ProductItemObject>() {
        @Override
        public ProductItemObject createFromParcel(Parcel in) {
            return new ProductItemObject(in);
        }

        @Override
        public ProductItemObject[] newArray(int size) {
            return new ProductItemObject[size];
        }
    };

    public boolean isChosen() {
        return isChosen;
    }

    public void setChosen(boolean chosen) {
        isChosen = chosen;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBoolean(isChosen);
        dest.writeString(productName);
        dest.writeDouble(qty);
        dest.writeString(id);
    }
}

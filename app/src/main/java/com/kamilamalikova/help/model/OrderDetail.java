package com.kamilamalikova.help.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderDetail implements Parcelable {
    private long id;

    private Product product;

    private double quantity;

    private double cost;

    public OrderDetail(JSONObject object) throws JSONException {
        this.id = object.getInt("id");
        this.product = new Product(object.getJSONObject("product"));
        this.quantity = object.getDouble("quantity");
        this.cost = object.getDouble("cost");
    }

    protected OrderDetail(Parcel in) {
        id = in.readLong();
        product = in.readParcelable(Product.class.getClassLoader());
        quantity = in.readDouble();
        cost = in.readDouble();
    }

    public static final Creator<OrderDetail> CREATOR = new Creator<OrderDetail>() {
        @Override
        public OrderDetail createFromParcel(Parcel in) {
            return new OrderDetail(in);
        }

        @Override
        public OrderDetail[] newArray(int size) {
            return new OrderDetail[size];
        }
    };

    public long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeParcelable(this.product, flags);
        dest.writeDouble(this.quantity);
        dest.writeDouble(this.cost);
    }
}

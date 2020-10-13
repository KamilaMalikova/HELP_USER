package com.kamilamalikova.help.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class Product implements Parcelable {

    protected long id;

    protected String productName;

    private double inStockQty;

    private boolean activeStatus;

    private LocalDateTime createdAt;

    private boolean restaurant;

    protected Unit unit;

    protected Category category;

    protected double cost;

    protected double buyQty = 0.0;

    public Product(long id, String productName, double inStockQty, boolean activeStatus, boolean restaurant, Unit unit, Category category, double cost) {
        this.id = id;
        this.productName = productName;
        this.inStockQty = inStockQty;
        this.activeStatus = activeStatus;
        this.restaurant = restaurant;
        this.unit = unit;
        this.category = category;
        this.cost = cost;
    }

    protected Product(Parcel in) {
        id = in.readLong();
        productName = in.readString();
        inStockQty = in.readDouble();
        activeStatus = in.readByte() != 0;
        restaurant = in.readByte() != 0;
        cost = in.readDouble();
        buyQty = in.readDouble();
    }

     public Product(JSONObject object) throws JSONException {
        this.id = object.getInt("id");
        this.productName = object.getString("productName");
        this.inStockQty = object.getDouble("inStockQty");
        this.activeStatus = object.getBoolean("activeStatus");
        this.restaurant = object.getBoolean("restaurant");
        this.unit = new Unit((JSONObject)object.get("unit"));
        this.category = new Category((JSONObject)object.get("category"));
        this.cost = object.getDouble("cost");
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getInStockQty() {
        return inStockQty;
    }

    public void setInStockQty(double inStockQty) {
        this.inStockQty = inStockQty;
    }

    public boolean isActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isRestaurant() {
        return restaurant;
    }

    public void setRestaurant(boolean restaurant) {
        this.restaurant = restaurant;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getBuyQty() {
        return buyQty;
    }

    public void setBuyQty(double buyQty) {
        this.buyQty = buyQty;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.productName);
        dest.writeDouble(this.inStockQty);
        dest.writeDouble(this.cost);
        dest.writeValue(this.createdAt);
        dest.writeValue(this.category);
        dest.writeValue(this.unit);
        dest.writeDouble(this.buyQty);

    }
}

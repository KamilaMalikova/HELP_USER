package com.kamilamalikova.help.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class StockItemBalance implements Parcelable {
    private long id;

    private String name;

    private Unit unit;

    private Category category;

    private double inStockQty;

    private long productId;

    private boolean restaurant;

    public StockItemBalance(long id, String name, Unit unit, Category category, double inStockQty, long productId, boolean restaurant) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.category = category;
        this.inStockQty = inStockQty;
        this.productId = productId;
        this.restaurant = restaurant;
    }

    public StockItemBalance(JSONObject object) throws JSONException {
        this.id = object.getLong("id");
        this.name = object.getString("name");
        this.unit = new Unit(object.getJSONObject("unit"));
        this.category = new Category(object.getJSONObject("category"));
        this.inStockQty = object.getDouble("inStockQty");
        this.productId = object.getLong("productId");
        this.restaurant = object.getBoolean("restaurant");
    }

    protected StockItemBalance(Parcel in) {
        id = in.readLong();
        name = in.readString();
        unit = in.readParcelable(Unit.class.getClassLoader());
        category = in.readParcelable(Category.class.getClassLoader());
        inStockQty = in.readDouble();
        productId = in.readLong();
        restaurant = in.readByte() != 0;
    }

    public static final Creator<StockItemBalance> CREATOR = new Creator<StockItemBalance>() {
        @Override
        public StockItemBalance createFromParcel(Parcel in) {
            return new StockItemBalance(in);
        }

        @Override
        public StockItemBalance[] newArray(int size) {
            return new StockItemBalance[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getInStockQty() {
        return inStockQty;
    }

    public void setInStockQty(double inStockQty) {
        this.inStockQty = inStockQty;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public boolean isRestaurant() {
        return restaurant;
    }

    public void setRestaurant(boolean restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeParcelable(this.unit, flags);
        dest.writeParcelable(this.category, flags);
        dest.writeDouble(this.inStockQty);
        dest.writeLong(this.productId);
        dest.writeBoolean(this.restaurant);
    }


    public JSONObject generateJSON() throws JSONException {
        String json = "{\n" +
                "  \"id\": "+id+",\n" +
                "  \"name\": \""+name+"\",\n" +
                "  \"unit\": {\n" +
                "    \"id\": "+unit.getId()+",\n" +
                "    \"unitName\": \""+unit.getUnitName()+"\"\n" +
                "  },\n" +
                "  \"category\": {\n" +
                "    \"id\": "+category.getId()+",\n" +
                "    \"category\": \""+category.getCategory()+"\"\n" +
                "  },\n" +
                "  \"inStockQty\": "+inStockQty+",\n" +
                "  \"productId\": "+productId+",\n" +
                "  \"restaurant\": "+restaurant+"\n" +
                "}";

        return new JSONObject(json);
    }
}

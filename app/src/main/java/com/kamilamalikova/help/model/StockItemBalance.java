package com.kamilamalikova.help.model;

public class StockItemBalance {
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
}

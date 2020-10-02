package com.kamilamalikova.help.model;

import java.time.LocalDateTime;

public class Product {

    protected long id;

    protected String productName;

    private double inStockQty;

    private boolean activeStatus;

    private LocalDateTime createdAt;

    private boolean restaurant;

    protected Unit unit;

    protected Category category;

    protected double cost;

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
}

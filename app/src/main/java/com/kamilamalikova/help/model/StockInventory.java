package com.kamilamalikova.help.model;

public class StockInventory {

    private long productId;

    private String productName;

    private double amount;

    public StockInventory(long productId, String productName, double amount) {
        this.productId = productId;
        this.productName = productName;
        this.amount = amount;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}

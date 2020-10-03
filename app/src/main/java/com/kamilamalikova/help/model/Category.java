package com.kamilamalikova.help.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {
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
}

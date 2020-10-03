package com.kamilamalikova.help.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Unit {
    private int id;

    private String unitName;

    public Unit(int id, String unitName) {
        this.id = id;
        this.unitName = unitName;
    }

    public Unit(JSONObject unit) throws JSONException {
        this.id = unit.getInt("id");
        this.unitName = unit.getString("unitName");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}

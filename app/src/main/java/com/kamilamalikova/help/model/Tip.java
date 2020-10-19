package com.kamilamalikova.help.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Tip {
    private int id;
    private double tip;

    public Tip(JSONObject object) throws JSONException {
        this.id = object.getInt("id");
        this.tip = object.getDouble("tip");
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTip() {
        return tip;
    }

    public void setTip(double tip) {
        this.tip = tip;
    }
}

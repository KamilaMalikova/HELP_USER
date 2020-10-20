package com.kamilamalikova.help.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderReport {
    List<OrderDetail> orderDetails;
    double sum;
    double tip_sum;
    double all_sum;
    Tip tip;

    public OrderReport(JSONObject object) throws JSONException {
        this.orderDetails = new ArrayList<>();
        if (!object.isNull("orderDetails")){
            JSONArray jsonArray = object.getJSONArray("orderDetails");
            for (int i = 0; i< jsonArray.length(); i++){
                this.orderDetails.add(new OrderDetail(jsonArray.getJSONObject(i)));
            }
        }
        this.sum = object.getDouble("sum");
        this.tip_sum = object.getDouble("tip_sum");
        this.all_sum = object.getDouble("all_sum");
        this.tip = new Tip(object.getJSONObject("tip"));
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public double getSum() {
        return sum;
    }

    public double getTip_sum() {
        return tip_sum;
    }

    public double getAll_sum() {
        return all_sum;
    }

}

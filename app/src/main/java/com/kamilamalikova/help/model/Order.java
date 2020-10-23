package com.kamilamalikova.help.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class Order implements Parcelable {
    private long orderId;

    private int tableId;

    private String waiterUsername;

    private String waiterName;

    private OrderStatus orderStatus; // opened -> closed

    private LocalDateTime dateTime;

    private List<OrderDetail> orderDetails;

    public Order(JSONObject response) throws JSONException {
        this.orderId = response.getInt("orderId");
        this.tableId = response.getInt("tableId");
        this.waiterUsername = response.getString("waiterUsername");
        this.waiterName = response.getString("waiterName");
        this.orderStatus = (response.getString("orderStatus").equals(OrderStatus.CREATED.name()) ? OrderStatus.CREATED : OrderStatus.CLOSED);
        this.dateTime = LocalDateTime.parse(response.getString("createdAt"));

        this.orderDetails = new ArrayList<>();

        try {
            JSONArray array = response.getJSONArray("orderDetails");
            if (array.length() > 0){
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    orderDetails.add(new OrderDetail(object));
                }
                insertionSort(orderDetails);
            }

        }catch (Exception ex){
            this.orderDetails = new ArrayList<>();
            ex.printStackTrace();
        }

    }

    public static void insertionSort(List<OrderDetail> array) {
        for (int i = 1; i < array.size(); i++) {
            OrderDetail current = array.get(i);
            int j = i - 1;
            while(j >= 0 && current.getId() < array.get(i).getId()) {
                array.set(j+1, array.get(j));
                j--;
            }

            array.set(j+1, current);
        }
    }

    protected Order(Parcel in) {
        orderId = in.readLong();
        tableId = in.readInt();
        waiterUsername = in.readString();
        waiterName = in.readString();
        orderDetails = in.createTypedArrayList(OrderDetail.CREATOR);
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public long getOrderId() {
        return orderId;
    }

    public int getTableId() {
        return tableId;
    }

    public String getWaiterUsername() {
        return waiterUsername;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }


    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public double getSum(){
        double sum = 0;
        for (OrderDetail orderDetail: orderDetails) {
            sum+=orderDetail.getCost();
        }
        return sum;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.orderId);
        dest.writeInt(this.tableId);
        dest.writeString(this.waiterName);
        dest.writeString(this.waiterUsername);
        dest.writeValue(this.orderStatus);
        dest.writeValue(this.dateTime);
        dest.writeParcelableList(this.orderDetails, flags);

    }
}

package com.kamilamalikova.help.model;

import android.content.Context;
import android.view.View;
import android.widget.Spinner;

import com.kamilamalikova.help.request.RequestPackage;
import com.kamilamalikova.help.request.RequestType;
import com.kamilamalikova.help.ui.stock.adapter.ProductItemObject;

import org.json.JSONException;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RequestFormer {
    public static RequestPackage getRequestPackage(Context context, String url, String productName, String category, boolean active, boolean restaurant){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.GET);
        requestPackage.setUrl(url);
        if (productName != null) requestPackage.setParam("productName", productName);
        if (!category.equals("0") && !(category.equals("500"))) requestPackage.setParam("category", category);
        if (!active) requestPackage.setParam("active", "0");
        if (!restaurant) requestPackage.setParam("restaurant", "0");
        requestPackage.getBytes();
        return requestPackage;
    }

    public static RequestPackage getRequestPackage(Context context, String url){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.GET);
        requestPackage.setUrl(url);
        requestPackage.getBytes();
        return requestPackage;
    }

    public static RequestPackage requestStockProductRequestPackage(Context context, String url, String productName, String categoryId, String category){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.GET);
        requestPackage.setUrl(url);
        if (productName != null) requestPackage.setParam("name", productName);
        if (!categoryId.equals("0") && !(categoryId.equals("500"))) {
            requestPackage.setParam("id", categoryId);
            requestPackage.setParam("category", category);
        }
        requestPackage.getBytes();
        return requestPackage;
    }

    public static RequestPackage getProductItemRequestPackage(Context context, String url, StockDocument stockDocument, List<ProductItemObject> productItemObjects) throws JSONException {
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url+"/"+stockDocument.getDocumentId());
        //Set ByteArrayEntity
        requestPackage.setEntity(requestPackage.getStockInventoryJSONArray(stockDocument, productItemObjects).toString());
        return requestPackage;
    }

    public static RequestPackage getDocumentRequestPackage(Context context, String url, int docId, String docType, LocalDateTime time){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url);
        requestPackage.setParam("documentId", docId);
        requestPackage.setParam("documentType", docType);
        requestPackage.setParam("date", time);
        requestPackage.getBytes();
        return requestPackage;
    }

    public static RequestPackage getFilterRequestPackage(Context context, String url, String type, LocalDateTime from, LocalDateTime to){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.GET);
        requestPackage.setUrl(url);

        if (type != null) requestPackage.setParam("type", type);
        if (from != null) requestPackage.setParam("from", from);
        if (to != null) requestPackage.setParam("to", to);

        requestPackage.getBytes();
        return requestPackage;
    }

    public static RequestPackage getFilterRequestPackage(Context context, String url, LocalDateTime from, LocalDateTime to){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.GET);
        requestPackage.setUrl(url);
        if (from != null) requestPackage.setParam("from", from);
        if (to != null) requestPackage.setParam("to", to);
        requestPackage.getBytes();
        return requestPackage;
    }

    public static RequestPackage getProductRequestPackage(Context context, String url, String productName, String categoryId, String category){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.GET);
        requestPackage.setUrl(url);

        if (productName != null) requestPackage.setParam("name", productName);
        if (!categoryId.equals("0") && !(categoryId.equals("500"))) {
            requestPackage.setParam("id", categoryId);
            requestPackage.setParam("category", category);
        }
        return requestPackage;
    }

    public static RequestPackage getProductRequestPackage(Context context, String url, Product product){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url+"/"+product.getId());

        requestPackage.setParam("productName", product.getProductName());
        requestPackage.setParam("restaurant", product.isRestaurant());
        requestPackage.setParam("inStockQty", Double.toString(product.getInStockQty()));
        requestPackage.setParam("unit", product.getUnit().getId());
        requestPackage.setParam("category", product.getCategory().getId());
        requestPackage.setParam("active", product.isActiveStatus());
        requestPackage.setParam("cost", Double.toString(product.getCost()));
        requestPackage.getBytes();
        return requestPackage;
    }

    public static RequestPackage getProductRequestPackage(Context context, String productName, String cost, String restaurant, String unit, String category){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl("/products");
        requestPackage.setParam("productName", productName);
        requestPackage.setParam("restaurant", restaurant);
        requestPackage.setParam("unit", unit);
        requestPackage.setParam("category", category);
        requestPackage.setParam("cost", cost);
        requestPackage.getBytes();
        return requestPackage;
    }

    public static RequestPackage getStockItemRequestPackage(Context context, String url, StockItemBalance itemBalance) throws JSONException {
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url);
        requestPackage.setEntity(requestPackage.getJsonObject(itemBalance).toString());
        return requestPackage;
    }

    public static RequestPackage getOrdersRequestPackage(Context context, String url, OrderStatus status, String username, LocalDateTime from, LocalDateTime to, int tableId){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.GET);
        requestPackage.setUrl(url);

        if (status != null && status != OrderStatus.ALL) requestPackage.setParam("status", status.name());
        if (username != null) requestPackage.setParam("username", username);
        if (from != null) requestPackage.setParam("start", from);
        if (to != null) requestPackage.setParam("end", to);
        if (tableId != 0) requestPackage.setParam("tableId", tableId);
        requestPackage.getBytes();
        return requestPackage;
    }

    public static RequestPackage getUsersRequestPackage(Context context, String url, String query, Role role){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.GET);
        requestPackage.setUrl(url);
        if (query != null) requestPackage.setParam("query", query);
        if (role != null) requestPackage.setParam("role", role.name());
        requestPackage.getBytes();

        return requestPackage;
    }

    public static RequestPackage getUserRequestPackage(Context context, String url, User user) throws JSONException {
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url);

        if(user.getRole() == Role.NOTWORKING){
            user.setDeleted(true);
        }
        requestPackage.setEntity(user.generateJsonObject().toString());
        return requestPackage;
    }

    public static RequestPackage getRequestPackageWithKey(Context context, String url, String key, String value){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url);
        requestPackage.setParam(key, value);
        requestPackage.getBytes();
        return requestPackage;
    }

    public static RequestPackage getSettingsRequestPackage(Context context, String url, String key, String value){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url);
        if (value != null && key != null) requestPackage.setParam(key, value);
        requestPackage.getBytes();
        return requestPackage;
    }

    public static RequestPackage getRequestPackage(Context context, String url, EatingPlace eatingPlace){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url);
        requestPackage.setParam("tableId", eatingPlace.getId()+"");
        requestPackage.setParam("username", eatingPlace.getWaiterUsername());
        return requestPackage;
    }

    public static RequestPackage getRequestPackage(Context context, String url, boolean reserved){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url);
        requestPackage.setParam("reserved", reserved);
        return requestPackage;
    }

    public static RequestPackage getRequestPackage(Context context, String url, Order order, Set<Product> products) throws JSONException {
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url);

        List<Product> productsToSave = new ArrayList<>();
        for (Product product: products) {
            if (product.getBuyQty() != 0.0) productsToSave.add(product);
        }

        requestPackage.setEntity(requestPackage.getOrderDetailJSONArray(order, productsToSave).toString());
        return requestPackage;
    }

    public static RequestPackage getRequestPackage(Context context, String url, long orderId){
        RequestPackage requestPackage = new RequestPackage(context);
        requestPackage.setMethod(RequestType.POST);
        requestPackage.setUrl(url+"/"+orderId);

        return requestPackage;
    }
}

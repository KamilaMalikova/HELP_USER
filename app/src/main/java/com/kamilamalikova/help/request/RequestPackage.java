package com.kamilamalikova.help.request;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.kamilamalikova.help.model.Order;
import com.kamilamalikova.help.model.Product;
import com.kamilamalikova.help.model.StockDocument;
import com.kamilamalikova.help.model.StockItemBalance;
import com.kamilamalikova.help.ui.stock.adapter.ProductItemObject;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestPackage {
    private final String prefix = "http://";

    private final String server = "192.168.25.107";

    private final String port = "8080";

    private String url;

    private RequestType method;

    private Map<String, String> params = new HashMap<>();

    private String authorizationToken = "";



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = prefix+server+":"+
                port+url;
    }

    public RequestType getMethod() {
        return method;
    }

    public void setMethod(RequestType method) {
        this.method = method;
    }

    public String getServer() {
        return server;
    }

    public String getPort() {
        return port;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void setParam(String key, String value) {
        this.params.put(key, value);
    }

    public JSONObject getJsonObject(){
        return new JSONObject(params);
    }

    public JSONObject getJsonObject(StockItemBalance stockItemBalance) throws JSONException {
        return new JSONObject(createStockItemJson(stockItemBalance));
    }


    private String createStockItemJson(StockItemBalance stockItemBalance){
        String json = "{" +
                "\"id\":"+ stockItemBalance.getId() +","+
                "\"name\" :"+ "\""+stockItemBalance.getName() +"\","+
                "\"unit\":"+ "{"+
                        "\"id\":"+ stockItemBalance.getUnit().getId()+ ","+
                        "\"unitName\":"+ "\""+stockItemBalance.getUnit().getUnitName()+"\""+
                "},"+
                "\"category\":"+ "{"+
                        "\"id\":"+ stockItemBalance.getCategory().getId()+","+
                        "\"category\":"+ "\""+stockItemBalance.getCategory().getCategory()+"\"" +
                "},"+
                "\"inStockQty\":"+ 0+","+
                "\"productId\":"+ 0+","+
                "\"restaurant\":" +stockItemBalance.isRestaurant()+
                "}";
        return json;
    }

    public JSONArray getStockInventoryJSONArray(StockDocument stockDocument, List<ProductItemObject> itemObjects) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (ProductItemObject itemObject:itemObjects) {
            jsonArray.put(createInventoryObject(stockDocument, itemObject));
        }
        return jsonArray;
    }

    private JSONObject createInventoryObject(StockDocument stockDocument, ProductItemObject itemObject) throws JSONException {
        String json = "{\n" +
                "    \"id\": 0,\n" +
                "    \"stockDocument\": {\n" +
                "      \"documentId\": "+stockDocument.getDocumentId()+",\n" +
                "      \"documentType\": \""+stockDocument.getDocumentType()+"\",\n" +
                "      \"date\": \""+stockDocument.getDate()+"\",\n" +
                "      \"inventories\": [\n" +
                "        null\n" +
                "      ]\n" +
                "    },\n" +
                "    \"productId\": "+itemObject.getId()+",\n" +
                "    \"productName\": \""+itemObject.getProductName()+"\",\n" +
                "    \"amount\": "+itemObject.getQty()+"\n" +
                "  }";
        return new JSONObject(json);
    }

    public JSONArray getOrderDetailJSONArray(Order order, List<Product> products) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Product product: products) {
            jsonArray.put(getOrderDetailJSONObject(order, product));
        }
        return jsonArray;
    }

    private JSONObject getOrderDetailJSONObject(Order order, Product product) throws JSONException {
        String json = "{\n" +
                "        \"orderId\":"+order.getOrderId()+",\n" +
                "        \"productId\":"+product.getId()+",\n" +
                "        \"qty\":"+product.getBuyQty()+"\n" +
                "    }";
        return new JSONObject(json);
    }

    public String getEncodedParams(){
        if (!method.equals(RequestType.GET)) return url;

        StringBuilder sb = new StringBuilder();
        for (String key: params.keySet()) {
            String value = null;
            try {
                value = URLEncoder.encode(params.get(key), "UTF-8");
            }catch (UnsupportedEncodingException ex){
                ex.printStackTrace();
            }
            if (sb.length() > 0){
                sb.append("&");
            }
            sb.append(key+"="+value);
        }
        return sb.toString();
    }

    public String getFullUrl(){
        if (!method.equals(RequestType.GET)) return url;
        else if (params != null || !params.isEmpty())return this.url+"/?"+getEncodedParams();
        else return url;
    }


    public RequestParams getRequestParams(){
        return new RequestParams(params);
    }
}

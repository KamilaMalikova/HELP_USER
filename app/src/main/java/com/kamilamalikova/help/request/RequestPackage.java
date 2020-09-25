package com.kamilamalikova.help.request;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
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
        this.url = prefix+server+":"+port+url;
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
        return this.url+"/?"+getEncodedParams();
    }
}

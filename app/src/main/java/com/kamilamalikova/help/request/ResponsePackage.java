package com.kamilamalikova.help.request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResponsePackage {
    public String authorizationToken;

    public JSONObject jsonObjectResult;

    public JSONArray jsonArrayResult;

    public String stringResponse;

    public int code = 0;

    public ResponsePackage() {
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }


    public JSONObject getJsonObjectResult() {
        return jsonObjectResult;
    }


    public JSONArray getJsonArrayResult() {
        return jsonArrayResult;
    }


    public String getResultParam(String key) throws JSONException {
        return (String) jsonObjectResult.get(key);
    }

    public JSONObject getArrayParam(int index) throws JSONException {
        return jsonArrayResult.getJSONObject(index);
    }

    public String getStringResponse() {
        return stringResponse;
    }

    public void setStringResponse(String stringResponse) {
        this.stringResponse = stringResponse;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    public void setJsonObjectResult(JSONObject jsonObjectResult) {
        this.jsonObjectResult = jsonObjectResult;
    }

    public void setJsonArrayResult(JSONArray jsonArrayResult) {
        this.jsonArrayResult = jsonArrayResult;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

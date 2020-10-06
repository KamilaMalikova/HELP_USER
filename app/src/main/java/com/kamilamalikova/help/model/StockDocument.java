package com.kamilamalikova.help.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDateTime;


import java.util.ArrayList;
import java.util.List;

public class StockDocument {
    private long documentId;

    private String documentType;

    private LocalDateTime date;

    private List<StockInventory> inventories = new ArrayList<>();

    public StockDocument(long documentId, String documentType, LocalDateTime date, List<StockInventory> inventories) {
        this.documentId = documentId;
        this.documentType = documentType;
        this.date = date;
        this.inventories = inventories;
    }


    public StockDocument(JSONObject object) throws JSONException {
        this.documentId = object.getInt("documentId");
        this.documentType = object.getString("documentType");
        this.date = LocalDateTime.parse(object.getString("date"));

    }

    public long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}

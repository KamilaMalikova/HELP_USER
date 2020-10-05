package com.kamilamalikova.help.ui.stock.adapter;

import java.time.LocalDateTime;

public class ItemObject {
    String docId;
    String type;
    LocalDateTime dateTime;

    public ItemObject(String docId, String type, LocalDateTime dateTime) {
        this.docId = docId;
        this.type = type;
        this.dateTime = dateTime;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}

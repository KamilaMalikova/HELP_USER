package com.kamilamalikova.help.ui.products.adapter;

public class ItemObject {
    private String id;
    private String value;

    public ItemObject(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}

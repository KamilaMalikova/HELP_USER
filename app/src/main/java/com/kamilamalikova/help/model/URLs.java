package com.kamilamalikova.help.model;

public enum  URLs {
    GET_CATEGORIES("/categories"),
    GET_UNITS("/units"),
    GET_PRODUCTS("/products");

    String name;

    URLs(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

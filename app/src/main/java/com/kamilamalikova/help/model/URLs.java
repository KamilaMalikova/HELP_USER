package com.kamilamalikova.help.model;

public enum  URLs {
    GET_CATEGORIES("/categories"),
    GET_UNITS("/units"),
    GET_PRODUCTS("/products"),
    GET_PRODUCT("/products/product"),
    POST_PRODUCT_UPDATE("/products/product");

    String name;

    URLs(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

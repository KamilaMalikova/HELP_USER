package com.kamilamalikova.help.model;

public enum  URLs {
    GET_CATEGORIES("/categories"),
    GET_UNITS("/units"),
    GET_PRODUCTS("/products"),
    GET_PRODUCT("/products/product"),
    POST_PRODUCT_UPDATE("/products/product"),
    POST_ITEMS("/items"),
    GET_ITEMS("/items"),
    POST_DOC("/documents"),
    POST_INVENTORY("/inventory"),
    GET_DOCS("/documents"),
    GET_TABLES("/tables"),
    GET_MENU_ORDER("/menu/order"),
    POST_TABLE("/tables/table"),
    GET_ORDER("/orders/order"),
    POST_ORDER("/orders/order"),
    GET_ORDERS("/orders"),
    POST_ORDERS("/orders"),
    POST_ORDER_DETAIL("/order-details"),
    POST_TABLES("/tables"),
    POST_TABLES_DELETE("/tables/delete"),
    POST_USER("/users"),
    GET_USERS("/users"),
    GET_TIP("/tip"),
    POST_TIP("/tip");

    String name;

    URLs(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}

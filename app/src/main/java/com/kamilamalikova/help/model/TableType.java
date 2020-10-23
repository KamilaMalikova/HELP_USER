package com.kamilamalikova.help.model;

public enum TableType {
    ALL("Все"), RESERVED("Занятые"), FREE("Свободные");

    String ru_name;

    TableType(String ru_name) {
        this.ru_name = ru_name;
    }

    public String getRu_name() {
        return ru_name;
    }
}

package com.kamilamalikova.help.model;

public enum DOCTYPE {
    IN("In", "Приход"), OUT("Out", "Расход");

    private String name;
    private String name_ru;

    DOCTYPE(String name, String name_ru) {
        this.name = name;
        this.name_ru = name_ru;
    }

    public String getName() {
        return name;
    }

    public String getName_ru() {
        return name_ru;
    }
}
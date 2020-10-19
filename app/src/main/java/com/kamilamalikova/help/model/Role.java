package com.kamilamalikova.help.model;

import android.os.Parcel;
import android.os.Parcelable;

public enum Role {
    ADMIN("Администратор"), OWNER("Владелец"), STUFF("Сотрудник склада"), WAITER("Официант"), NOTWORKING("Уволен"), ALL("");
    String ru_name;
    Role(String ru_name){
        this.ru_name = ru_name;
    }

    public String getRu_name() {
        return ru_name;
    }

}

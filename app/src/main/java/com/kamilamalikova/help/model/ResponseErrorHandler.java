package com.kamilamalikova.help.model;

import android.content.Context;
import android.widget.Toast;

import com.kamilamalikova.help.R;

public class ResponseErrorHandler {
    public static void showErrorMessage(Context context, int code){
        String message = "";
        switch (code){
            case 403:
                message = "Не достатовно прав для выполнения операции!";
                break;
            case 409:
                message = "Конфликт запроса! Возможно такие данные уже имеются";
                break;
            case 400:
                message = "Не достаточно данных!";
                break;
            case 404:
                message = "Данные не найдены!";
                break;
            case 406:
                message = "Неправильный запрос!";
                break;
            case 424:
                message = "Данные не могут быть удаленны, так как используются в других разделах! Возможно только изменить";
                break;
            default:
                message = code+" - ошибка";
        }
        showErrorMessage(context, message);
    }

    public static void showErrorMessage(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT)
                .show();
    }
}

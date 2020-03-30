package com.joelinjatovo.navette.database.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ObjectConverter {
    @TypeConverter
    public static Object fromString(String value) {
        Type listType = new TypeToken<Object>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromObject(Object list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}

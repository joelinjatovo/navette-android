package com.navetteclub.api.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.navetteclub.utils.Log;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTypeSerializer implements JsonSerializer<Date> {

    private final String TAG = DateTypeSerializer.class.getSimpleName();

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'";

    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.FRANCE);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateFormatAsString = formatter.format(date);
        return new JsonPrimitive(dateFormatAsString);
    }

}

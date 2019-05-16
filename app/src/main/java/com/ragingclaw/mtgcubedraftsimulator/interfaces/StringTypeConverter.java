package com.ragingclaw.mtgcubedraftsimulator.interfaces;

import androidx.room.TypeConverter;


public class StringTypeConverter {
    @TypeConverter
    public static String toString(String[] strings) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : strings) {
            stringBuilder.append(s).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    @TypeConverter
    public static String[] fromString(String value) {
        return value.split(",");
    }
}

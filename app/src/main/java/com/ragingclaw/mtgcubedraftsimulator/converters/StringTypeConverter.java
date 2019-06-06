package com.ragingclaw.mtgcubedraftsimulator.converters;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;

public class StringTypeConverter {

    @TypeConverter
    public String fromArray(ArrayList<String> strings) {
        StringBuilder string = new StringBuilder();
        if (strings != null) {
            for (String s : strings) string.append(s).append(",");
        }
        return string.toString();
    }

    @TypeConverter
    public ArrayList<String> toArray(String concatenatedStrings) {

        return new ArrayList<>(Arrays.asList(concatenatedStrings.split(",")));
    }

}

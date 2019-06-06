package com.ragingclaw.mtgcubedraftsimulator.converters;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class StringTypeConverter {

    @TypeConverter
    public String fromArray(ArrayList<String> strings) {
        String string = "";
        if (strings != null) {
            for (String s : strings) string += (s + ",");
        }
        return string;
    }

    @TypeConverter
    public ArrayList<String> toArray(String concatenatedStrings) {
        ArrayList<String> myStrings = new ArrayList<>();

        myStrings.addAll(Arrays.asList(concatenatedStrings.split(",")));

        return myStrings;
    }

}

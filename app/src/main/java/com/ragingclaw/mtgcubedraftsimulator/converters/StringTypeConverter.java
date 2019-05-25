package com.ragingclaw.mtgcubedraftsimulator.converters;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Collections;

public class StringTypeConverter {

    @TypeConverter
    public String fromArray(String[] strings) {
        String string = "";
        for(String s : strings) {
            string += (s + ",");
        }

        return string;
    }

    @TypeConverter
    public String[] toArray(String concatenatedStrings) {
        ArrayList<String> myStrings = new ArrayList<>();
        Collections.addAll(myStrings, concatenatedStrings.split(","));
        return (String[]) myStrings.toArray();
    }
}

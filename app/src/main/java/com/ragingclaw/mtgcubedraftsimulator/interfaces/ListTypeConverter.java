package com.ragingclaw.mtgcubedraftsimulator.interfaces;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;

import java.lang.reflect.Type;
import java.util.List;


public class ListTypeConverter {

    @TypeConverter
    public static List<Cube> stringToList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Cube>>() {}.getType();
        List<Cube> cubes = gson.fromJson(json, type);
        return cubes;
    }

    @TypeConverter
    public static String listToString(List<Cube> steps) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Cube>>() {}.getType();
        String json = gson.toJson(steps, type);
        return json;
    }
}

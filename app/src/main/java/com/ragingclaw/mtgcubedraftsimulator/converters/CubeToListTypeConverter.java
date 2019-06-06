package com.ragingclaw.mtgcubedraftsimulator.converters;

import androidx.room.TypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.ragingclaw.mtgcubedraftsimulator.database.Cube;

import java.lang.reflect.Type;
import java.util.List;


@SuppressWarnings("ALL")
public class CubeToListTypeConverter {

    @TypeConverter
    public static List<Cube> stringToList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Cube>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String listToString(List<Cube> steps) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Cube>>() {}.getType();
        return gson.toJson(steps, type);
    }
}

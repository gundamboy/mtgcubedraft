package com.ragingclaw.mtgcubedraftsimulator.interfaces;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ragingclaw.mtgcubedraftsimulator.models.Card;
import com.ragingclaw.mtgcubedraftsimulator.models.Draft;

import java.lang.reflect.Type;
import java.util.List;

public class TempConverters {
    @TypeConverter
    public static List<Card> stringToCards(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Card>>() {}.getType();
        List<Card> cards = gson.fromJson(json, type);
        return cards;
    }

    @TypeConverter
    public static String cardsToString(List<Card> list) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Card>>() {}.getType();
        String json = gson.toJson(list, type);
        return json;
    }

    @TypeConverter
    public static List<Draft> stringToDraft(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Draft>>() {}.getType();
        List<Draft> drafts = gson.fromJson(json, type);
        return drafts;
    }

    @TypeConverter
    public static String draftToString(List<Draft> list) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Draft>>() {}.getType();
        String json = gson.toJson(list, type);
        return json;
    }

    @TypeConverter
    public static List<Integer> stringToIdList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Draft>>() {}.getType();
        List<Integer> ids = gson.fromJson(json, type);
        return ids;
    }

    @TypeConverter
    public static String intToIdString(List<Integer> list) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Integer>>() {}.getType();
        String json = gson.toJson(list, type);
        return json;
    }
}

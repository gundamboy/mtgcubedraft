package com.ragingclaw.mtgcubedraftsimulator.converters;

import androidx.room.TypeConverter;
import androidx.room.util.StringUtil;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;

public class IntegerIdTypeConverter {
    @TypeConverter
    public static List<Integer> stringToIntList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        return StringUtil.splitToIntList(data);
    }

    @TypeConverter
    public static String intListToString(List<Integer> list) {
        Timber.tag("fart").w("IntegerIdTypeConverter list size: %s", list.size());
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer i : list) {
            String s = Integer.toString(i);
            stringBuilder.append(s).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}

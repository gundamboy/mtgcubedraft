package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ragingclaw.mtgcubedraftsimulator.interfaces.IntegerIdTypeConverter;


import java.util.List;

@Entity(tableName = "cubes")
@TypeConverters(IntegerIdTypeConverter.class)
public class EntityCubes {

    @PrimaryKey(autoGenerate = true)
    private int cubeId;
    private String cube_name;
    private int total_cards;

    private List<Integer> card_ids = null;
}
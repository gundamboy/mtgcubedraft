package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ragingclaw.mtgcubedraftsimulator.interfaces.StringTypeConverter;

import java.util.List;

@Entity(tableName = "drafts",
        foreignKeys = @ForeignKey(entity = EntityCubes.class, parentColumns = "cubeId",
                childColumns = "id",
                onDelete = ForeignKey.CASCADE))
@TypeConverters(StringTypeConverter.class)
public class EntityDrafts {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private List<Integer> booster_pack_one = null;
    private List<Integer> booster_pack_two = null;
    private List<Integer> booster_pack_three = null;
    private List<Integer> booster_choices = null;
    private List<Integer> draft_deck = null;

}

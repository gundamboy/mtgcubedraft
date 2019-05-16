package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ragingclaw.mtgcubedraftsimulator.interfaces.StringTypeConverter;

import java.util.List;

@Entity(tableName = "drafts",
        foreignKeys = @ForeignKey(entity = CubesEntity.class, parentColumns = "cubeId",
                childColumns = "id",
                onDelete = ForeignKey.CASCADE))
@TypeConverters(StringTypeConverter.class)
public class DraftsEntity {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int draftID;

    private int cubeId;

    private List<Integer> booster_choices = null;
    private List<Integer> draft_deck = null;

    public int getId() {
        return draftID;
    }

    public void setId(int draftID) {
        this.draftID = draftID;
    }

    public int getCubeId() {
        return cubeId;
    }

    public void setCubeId(int cubeId) {
        this.cubeId = cubeId;
    }

    public List<Integer> getBooster_choices() {
        return booster_choices;
    }

    public void setBooster_choices(List<Integer> booster_choices) {
        this.booster_choices = booster_choices;
    }

    public List<Integer> getDraft_deck() {
        return draft_deck;
    }

    public void setDraft_deck(List<Integer> draft_deck) {
        this.draft_deck = draft_deck;
    }
}

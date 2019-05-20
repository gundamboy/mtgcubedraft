package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ragingclaw.mtgcubedraftsimulator.interfaces.IntegerIdTypeConverter;
import com.ragingclaw.mtgcubedraftsimulator.interfaces.ListTypeConverter;

import java.util.List;

@Entity(tableName = "drafts",
        foreignKeys = @ForeignKey(entity = Cube.class, parentColumns = "cubeId",
                childColumns = "cubeId",
                onDelete = ForeignKey.CASCADE))
@TypeConverters(IntegerIdTypeConverter.class)
public class Draft {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int draftID;

    private int cubeId;
    private int totalSeats;

    @Nullable
    private List<Integer> booster_choices = null;

    @Nullable
    private List<Integer> draft_deck = null;

    public int getDraftID() {
        return draftID;
    }

    public void setDraftID(int draftID) {
        this.draftID = draftID;
    }

    public int getCubeId() {
        return cubeId;
    }

    public void setCubeId(int cubeId) {
        this.cubeId = cubeId;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
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

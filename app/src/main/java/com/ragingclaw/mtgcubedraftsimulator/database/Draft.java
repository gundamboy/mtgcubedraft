package com.ragingclaw.mtgcubedraftsimulator.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.ragingclaw.mtgcubedraftsimulator.converters.IntegerIdTypeConverter;

import java.util.List;

@Entity(tableName = "drafts",
        foreignKeys = @ForeignKey(entity = Cube.class, parentColumns = "cubeId",
                childColumns = "cubeId",
                onDelete = ForeignKey.CASCADE),
        indices = {
        @Index(name = "idx", value = {"cubeId"})
})
@TypeConverters(IntegerIdTypeConverter.class)
public class Draft {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int draftID;

    private int cubeId;
    private String draftName;
    private int totalSeats;
    private boolean draftComplete;
    private int completePercentage;
    private List<Integer> packIds;
    private List<Integer> draftDeckCardIds;

    public Draft(int draftID, int cubeId, String draftName, int totalSeats, boolean draftComplete, int completePercentage, List<Integer> packIds, List<Integer> draftDeckCardIds) {
        this.draftID = draftID;
        this.cubeId = cubeId;
        this.draftName = draftName;
        this.totalSeats = totalSeats;
        this.draftComplete = draftComplete;
        this.completePercentage = completePercentage;
        this.packIds = packIds;
        this.draftDeckCardIds = draftDeckCardIds;
    }

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

    public String getDraftName() {
        return draftName;
    }

    public void setDraftName(String draftName) {
        this.draftName = draftName;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public boolean isDraftComplete() {
        return draftComplete;
    }

    public void setDraftComplete(boolean draftComplete) {
        this.draftComplete = draftComplete;
    }

    public int getCompletePercentage() {
        return completePercentage;
    }

    public void setCompletePercentage(int completePercentage) {
        this.completePercentage = completePercentage;
    }

    public List<Integer> getPackIds() {
        return packIds;
    }

    public void setPackIds(List<Integer> packIds) {
        this.packIds = packIds;
    }

    public List<Integer> getDraftDeckCardIds() {
        return draftDeckCardIds;
    }

    public void setDraftDeckCardIds(List<Integer> draftDeckCardIds) {
        this.draftDeckCardIds = draftDeckCardIds;
    }
}
